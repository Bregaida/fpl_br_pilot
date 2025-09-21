package br.com.fplbr.pilot.auth.infrastructure.resource;

import br.com.fplbr.pilot.auth.application.service.AuthService;
import br.com.fplbr.pilot.auth.infrastructure.security.TotpService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base32;
import java.net.URLEncoder;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;
    
    @Inject
    TotpService totpService;
    
    // In-memory storage for TOTP sessions (in production, use Redis or database)
    private static final Map<String, TOTPSession> sessions = new ConcurrentHashMap<>();

    @POST
    @Path("/register")
    public Response register(Map<String, Object> body) {
        var out = authService.startRegistration(body);
        return Response.ok(out).build();
    }

    @POST
    @Path("/check-duplicate")
    public Response checkDuplicate(Map<String, Object> body) {
        LOG.info("Auth Resource: Verificando duplicatas - Body: " + body);
        try {
            String email = (String) body.get("email");
            String cpf = (String) body.get("cpf");
            
            LOG.info("Auth Resource: Email: " + email + ", CPF: " + cpf);
            
            // Usar o AuthService para verificar duplicatas
            var result = authService.checkDuplicates(email, cpf);
            LOG.info("Auth Resource: Resultado da verificação: " + result);
            
            if (result.containsKey("error")) {
                LOG.info("Auth Resource: Duplicata encontrada: " + result.get("error"));
                return Response.status(Response.Status.CONFLICT)
                        .entity(result)
                        .build();
            }
            
            LOG.info("Auth Resource: Nenhuma duplicata encontrada");
            return Response.ok(Map.of("ok", true)).build();
        } catch (Exception e) {
            LOG.error("Auth Resource: Erro ao verificar duplicatas: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/2fa/verify")
    public Response verify(Map<String, Object> body) {
        String email = (String) body.get("email");
        int code = ((Number) body.get("code")).intValue();
        authService.verifyAndCreate(email, code);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(Map<String, Object> body) {
        String login = (String) body.get("login");
        String password = (String) body.get("password");
        Integer totp = body.get("totp") == null ? null : ((Number) body.get("totp")).intValue();
        var out = authService.login(login, password, totp);
        if (Boolean.TRUE.equals(out.get("ok"))) {
            // Simplificado - sem JWT por enquanto
            return Response.ok(Map.of("ok", true, "message", "Login realizado com sucesso")).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(out).build();
    }

    @POST
    @Path("/login-totp-only")
    public Response loginTotpOnly(Map<String, Object> body) {
        try {
            String login = (String) body.get("login");
            int code = ((Number) body.get("code")).intValue();
            var out = authService.loginTotpOnly(login, code);
            if (Boolean.TRUE.equals(out.get("ok"))) {
                // Simplificado - sem JWT por enquanto
                return Response.ok(Map.of("ok", true, "message", "Login TOTP realizado com sucesso")).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity(out).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro interno do servidor: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/forgot")
    public Response forgot(Map<String, Object> body) {
        String login = (String) body.get("login");
        var out = authService.forgot(login);
        return Response.ok(out).build();
    }

    @POST
    @Path("/reset")
    public Response reset(Map<String, Object> body) {
        String tokenOrTemp = (String) body.get("tokenOrTemp");
        String newPassword = (String) body.get("newPassword");
        var out = authService.reset(tokenOrTemp, newPassword);
        return Response.ok(out).build();
    }

    // ===== TOTP/2FA APIs =====
    
    @POST
    @Path("/2fa/setup")
    @Operation(summary = "Prepare TOTP setup", description = "Generate TOTP secret and QR code for setup")
    public Response prepareTOTP(Map<String, Object> body) {
        LOG.info("Auth Resource: Preparando TOTP - Body: " + body);
        try {
            // Generate TOTP secret (fallback implementation if TotpService is null)
            String secret;
            if (totpService != null) {
                secret = totpService.generateBase32Secret();
            } else {
                // Fallback implementation
                secret = generateBase32SecretFallback();
            }
            
            // Create session
            String sessionId = UUID.randomUUID().toString();
            TOTPSession session = new TOTPSession(secret, false);
            sessions.put(sessionId, session);
            
            // Get email from request body
            String email = (String) body.get("email");
            if (email == null || email.trim().isEmpty()) {
                email = "user@example.com"; // fallback
            }
            
            // Build OTP Auth URL
            String otpauthUrl;
            if (totpService != null) {
                otpauthUrl = totpService.buildOtpAuthUrl(secret, "FPL BR Pilot", email);
            } else {
                otpauthUrl = buildOtpAuthUrlFallback(secret, "FPL BR Pilot", email);
            }
            
            // Generate QR Code (simplified - in production use a proper QR library)
            String qrPngBase64 = generateQRCode(otpauthUrl);
            
            // Mask secret for display
            String secretMasked = secret.substring(0, 4) + "..." + secret.substring(secret.length() - 4);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totpSessionId", sessionId);
            response.put("qrPngBase64", qrPngBase64);
            response.put("otpauthUrl", otpauthUrl);
            response.put("secretMasked", secretMasked);
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to prepare TOTP: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/2fa/verify-setup")
    @Operation(summary = "Verify TOTP code", description = "Verify the TOTP code during setup")
    public Response verifyTOTPSetup(Map<String, Object> body) {
        try {
            String sessionId = (String) body.get("totpSessionId");
            String code = (String) body.get("code");
            
            if (sessionId == null || code == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Session ID and code are required"))
                        .build();
            }
            
            TOTPSession session = sessions.get(sessionId);
            if (session == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Session not found"))
                        .build();
            }
            
            // For now, accept any 6-digit code (in production, implement proper TOTP verification)
            if (code.matches("\\d{6}")) {
                session.setVerified(true);
                return Response.ok(Map.of("ok", true)).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid code format"))
                        .build();
            }
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to verify TOTP: " + e.getMessage()))
                    .build();
        }
    }

    // Helper methods for TOTP
    private String generateQRCode(String data) {
        try {
            // Use qrserver.com API to generate QR code
            String encodedData = URLEncoder.encode(data, StandardCharsets.UTF_8);
            String qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodedData;
            
            // For now, return the API URL - frontend will handle the QR code generation
            // In production, you might want to fetch the image and return as base64
            return qrApiUrl;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }

    private String generateBase32SecretFallback() {
        SecureRandom random = new SecureRandom();
        byte[] secret = new byte[20];
        random.nextBytes(secret);
        Base32 base32 = new Base32();
        return base32.encodeAsString(secret).replace("=", "");
    }

    private String buildOtpAuthUrlFallback(String secretBase32, String issuer, String labelEmail) {
        try {
            String label = java.net.URLEncoder.encode(labelEmail, StandardCharsets.UTF_8);
            String iss = java.net.URLEncoder.encode(issuer, StandardCharsets.UTF_8);
            return "otpauth://totp/" + iss + ":" + label + "?secret=" + secretBase32 + "&issuer=" + iss + "&period=30&digits=6";
        } catch (Exception e) {
            return "otpauth://totp/FPLBR:" + labelEmail + "?secret=" + secretBase32 + "&issuer=FPLBR&period=30&digits=6";
        }
    }

    // Inner class for TOTP session
    private static class TOTPSession {
        private final String secret;
        private boolean verified;
        private final long createdAt;

        public TOTPSession(String secret, boolean verified) {
            this.secret = secret;
            this.verified = verified;
            this.createdAt = System.currentTimeMillis();
        }

        public String getSecret() {
            return secret;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }
}


