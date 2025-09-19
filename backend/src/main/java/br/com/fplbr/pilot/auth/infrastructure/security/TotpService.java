package br.com.fplbr.pilot.auth.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class TotpService {
    
    @Inject
    CryptoService crypto;

    public String generateBase32Secret() {
        // Generate a random 20-byte secret and encode as Base32
        SecureRandom random = new SecureRandom();
        byte[] secret = new byte[20];
        random.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret).replace("=", "");
    }

    public String buildOtpAuthUrl(String secretBase32, String issuer, String labelEmail) {
        String label = URLEncoder.encode(labelEmail, StandardCharsets.UTF_8);
        String iss = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        return "otpauth://totp/" + iss + ":" + label + "?secret=" + secretBase32 + "&issuer=" + iss + "&period=30&digits=6";
    }

    public boolean verify(String secretBase32, int code, long lastCounter) {
        // Temporary implementation - always returns false for now
        // TODO: Implement proper TOTP verification when dependencies are available
        return false;
    }

    public String encryptSecret(String base32) {
        return crypto.encrypt(base32);
    }

    public String decryptSecret(String encrypted) {
        return crypto.decrypt(encrypted);
    }
}


