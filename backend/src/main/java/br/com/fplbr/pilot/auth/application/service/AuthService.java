package br.com.fplbr.pilot.auth.application.service;

import br.com.fplbr.pilot.auth.infrastructure.persistence.*;
import br.com.fplbr.pilot.auth.infrastructure.security.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class AuthService {

    @Inject UserPanacheRepository users;
    @Inject PasswordTempRepository tempRepo;
    @Inject PasswordResetTokenRepository resetRepo;
    @Inject PasswordHasher hasher;
    @Inject TotpService totpService;
    @Inject CryptoService crypto;
    @Inject JwtService jwtService;
    @Inject PendingRegistrationStore pending;

    private static final DateTimeFormatter BIRTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public Map<String, Object> startRegistration(Map<String, Object> req) {
        String email = req.get("email") != null ? ((String) req.get("email")).toLowerCase(Locale.ROOT) : null;
        String password = (String) req.get("password");
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        if (UserEntity.find("lower(email)", email).firstResultOptional().isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        
        // Verificar CPF se fornecido
        String cpf = req.get("cpf") != null ? ((String) req.get("cpf")).replaceAll("[^0-9]","") : null;
        if (cpf != null && !cpf.isEmpty() && !cpf.equals("00000000000")) {
            if (UserEntity.find("cpf", cpf).firstResultOptional().isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
        }

        // Criar usuário imediatamente
        UserEntity u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setFullName(req.get("fullName") != null ? (String) req.get("fullName") : "Usuário");
        u.setEmail(email);
        u.setCpf(req.get("cpf") != null ? ((String) req.get("cpf")).replaceAll("[^0-9]","") : "00000000000");
        u.setPhoneDdd(req.get("phoneDdd") != null ? ((String) req.get("phoneDdd")).replaceAll("[^0-9]","") : "11");
        u.setPhoneNumber(req.get("phoneNumber") != null ? ((String) req.get("phoneNumber")).replaceAll("[^0-9]","") : "000000000");
        u.setCep(req.get("cep") != null ? ((String) req.get("cep")).replaceAll("[^0-9]","") : "00000000");
        u.setAddress(req.get("address") != null ? (String) req.get("address") : "Endereço");
        u.setAddressNumber(req.get("addressNumber") != null ? (String) req.get("addressNumber") : "0");
        u.setAddressComplement(req.get("addressComplement") != null ? (String) req.get("addressComplement") : "");
        u.setNeighborhood(req.get("neighborhood") != null ? (String) req.get("neighborhood") : "Bairro");
        u.setCity(req.get("city") != null ? (String) req.get("city") : "Cidade");
        u.setUf(req.get("uf") != null ? (String) req.get("uf") : "SP");
        u.setBirthDate(req.get("birthDate") != null ? java.time.LocalDate.parse((String) req.get("birthDate"), BIRTH_FMT) : java.time.LocalDate.now());
        u.setMaritalStatus(req.get("maritalStatus") != null ? (String) req.get("maritalStatus") : "Solteiro");
        u.setPilotType(req.get("pilotType") != null ? (String) req.get("pilotType") : "Privado");
        u.setCompany((String) req.get("company"));
        u.setLoginAlias(req.get("loginAlias") != null ? ((String) req.get("loginAlias")).toLowerCase(Locale.ROOT) : email);
        u.setPasswordHash(hasher.hash(password));
        u.setPasswordPepperVer(1);
        u.setTwoFactorEnabled(false); // 2FA será ativado após verificação
        u.setTotpSecretEncrypted(null); // Será definido após verificação
        u.setTotpLastUsedCounter(-1L);
        u.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        u.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        u.setAuditVersion(1);
        UserEntity.persist(u);
        
        System.out.println("Usuário criado: " + u.getId() + " - " + u.getEmail());

        // Gerar secret TOTP para ativação
        String secret = totpService.generateBase32Secret();
        String otpauth = totpService.buildOtpAuthUrl(secret, "FPL BR", email);
        
        // Salvar secret temporariamente para ativação
        pending.put(email, Map.of("userId", u.getId().toString()), secret, OffsetDateTime.now().plusMinutes(15));
        
        Map<String, Object> out = new HashMap<>();
        out.put("otpauthUrl", otpauth);
        out.put("secretMasked", secret.substring(0, 4) + "****" + secret.substring(secret.length()-4));
        out.put("userId", u.getId().toString());
        return out;
    }

    @Transactional
    public void verifyAndCreate(String email, int code) {
        PendingRegistrationStore.PendingData p = pending.get(email);
        if (p == null) throw new IllegalStateException("Cadastro pendente não encontrado/expirado");
        boolean ok = totpService.verify(p.secretBase32, code, -1);
        if (!ok) throw new IllegalArgumentException("Código TOTP inválido");

        // Buscar usuário já criado
        String userId = (String) p.data.get("userId");
        UserEntity u = UserEntity.findById(UUID.fromString(userId));
        if (u == null) throw new IllegalStateException("Usuário não encontrado");

        // Ativar 2FA
        u.setTwoFactorEnabled(true);
        u.setTotpSecretEncrypted(totpService.encryptSecret(p.secretBase32));
        u.setTotpLastUsedCounter(-1L);
        u.setTwoFactorVerifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
        u.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        u.persist();
        
        pending.remove(email);
    }

    @Transactional
    public Map<String, Object> login(String login, String password, Integer totp) {
        var opt = users.findByEmailOrCpfOrAlias(login);
        UserEntity u = opt.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (u.getLockedUntil() != null && u.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new IllegalStateException("Usuário bloqueado temporariamente");
        }
        if (!hasher.verify(u.getPasswordHash(), password)) {
            u.setFailedLoginAttempts(u.getFailedLoginAttempts()+1);
            if (u.getFailedLoginAttempts() >= 5) {
                u.setLockedUntil(OffsetDateTime.now().plusMinutes(10));
                u.setFailedLoginAttempts(0);
            }
            u.setUpdatedAt(OffsetDateTime.now());
            return Map.of("ok", false, "reason", "senha_invalida");
        }
        if (u.isTwoFactorEnabled()) {
            if (totp == null) return Map.of("ok", false, "reason", "totp_requerido");
            String secret = totpService.decryptSecret(u.getTotpSecretEncrypted());
            Long lastCounter = u.getTotpLastUsedCounter();
            long last = lastCounter == null ? -1L : lastCounter;
            boolean ok = totpService.verify(secret, totp, last);
            if (!ok) return Map.of("ok", false, "reason", "totp_invalido");
            // atualiza anti-replay
            long counter = java.time.Instant.now().getEpochSecond() / 30;
            u.setTotpLastUsedCounter(counter);
        }
        u.setFailedLoginAttempts(0);
        u.setLastLoginAt(OffsetDateTime.now());
        u.setUpdatedAt(OffsetDateTime.now());
        String token = jwtService.issueAccessToken(u.getId().toString(), Set.of("user"));
        return Map.of("ok", true, "jwt", token, "userId", u.getId().toString());
    }

    @Transactional
    public Map<String, Object> loginTotpOnly(String login, int code) {
        var opt = users.findByEmailOrCpfOrAlias(login);
        UserEntity u = opt.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (!u.isTwoFactorEnabled()) throw new IllegalStateException("2FA não habilitado");
        String secret = totpService.decryptSecret(u.getTotpSecretEncrypted());
        Long lastCounter = u.getTotpLastUsedCounter();
        long last = lastCounter == null ? -1L : lastCounter;
        if (!totpService.verify(secret, code, last)) throw new IllegalArgumentException("TOTP inválido");
        String token = jwtService.issueResetToken(u.getId().toString());
        return Map.of("ok", true, "resetJwt", token);
    }

    @Transactional
    public Map<String, Object> forgot(String login) {
        var opt = users.findByEmailOrCpfOrAlias(login);
        UserEntity u = opt.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        // Gera token aleatório e guarda hash
        String raw = UUID.randomUUID().toString().replace("-", "");
        PasswordResetTokenEntity e = new PasswordResetTokenEntity();
        e.setId(UUID.randomUUID());
        e.setUserId(u.getId());
        e.setTokenHash(hasher.hash(raw));
        e.setExpiresAt(OffsetDateTime.now().plusDays(2));
        e.setUsed(false);
        PasswordResetTokenEntity.persist(e);
        // Em produção, enviar e-mail com link contendo o token; aqui retornamos o token para testes
        return Map.of("ok", true, "token", raw);
    }

    @Transactional
    public Map<String, Object> reset(String tokenOrTemp, String newPassword) {
        // Tenta reset token
        var prt = resetRepo.list("used = false and expiresAt > now()");
        PasswordResetTokenEntity match = null;
        for (PasswordResetTokenEntity t : prt) {
            if (hasher.verify(t.getTokenHash(), tokenOrTemp)) { match = t; break; }
        }
        if (match == null) throw new IllegalArgumentException("Token inválido/expirado");
        UserEntity u = UserEntity.findById(match.getUserId());
        if (u == null) throw new IllegalStateException("Usuário não encontrado");
        u.setPasswordHash(hasher.hash(newPassword));
        u.setUpdatedAt(OffsetDateTime.now());
        match.setUsed(true);
        return Map.of("ok", true);
    }
}


