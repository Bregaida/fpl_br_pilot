package br.com.fplbr.pilot.auth.infrastructure.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base32;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class TotpService {
    
    @Inject
    CryptoService crypto;

    public String generateBase32Secret() {
        // Generate a random 20-byte secret and encode as Base32
        SecureRandom random = new SecureRandom();
        byte[] secret = new byte[20];
        random.nextBytes(secret);
        Base32 base32 = new Base32();
        return base32.encodeAsString(secret).replace("=", "");
    }

    public String buildOtpAuthUrl(String secretBase32, String issuer, String labelEmail) {
        String label = URLEncoder.encode(labelEmail, StandardCharsets.UTF_8);
        String iss = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        return "otpauth://totp/" + iss + ":" + label + "?secret=" + secretBase32 + "&issuer=" + iss + "&period=30&digits=6";
    }

    public boolean verify(String secretBase32, int code, long lastCounter) {
        try {
            // Get current time step
            long currentTimeStep = System.currentTimeMillis() / 1000 / 30;
            
            // Check current time step and previous time step (tolerance)
            return verifyCode(secretBase32, code, currentTimeStep) || 
                   verifyCode(secretBase32, code, currentTimeStep - 1) ||
                   verifyCode(secretBase32, code, currentTimeStep + 1);
        } catch (Exception e) {
            System.err.println("TOTP verification error: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verifyCode(String secretBase32, int code, long timeStep) {
        try {
            // Decode base32 secret
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secretBase32);
            
            // Create HMAC-SHA1
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA1");
            mac.init(secretKeySpec);
            
            // Create message (time step as 8-byte big-endian)
            byte[] message = new byte[8];
            for (int i = 7; i >= 0; i--) {
                message[i] = (byte) (timeStep & 0xff);
                timeStep >>= 8;
            }
            
            // Generate HMAC
            byte[] hash = mac.doFinal(message);
            
            // Extract 4-byte dynamic binary code
            int offset = hash[hash.length - 1] & 0xf;
            int binary = ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
            
            // Generate 6-digit code
            int totpCode = binary % 1000000;
            
            return totpCode == code;
        } catch (Exception e) {
            return false;
        }
    }

    public String encryptSecret(String base32) {
        return crypto.encrypt(base32);
    }

    public String decryptSecret(String encrypted) {
        return crypto.decrypt(encrypted);
    }
    
    // Método para debug - gera o código TOTP atual
    public String getCurrentCode(String secretBase32) {
        try {
            long currentTimeStep = System.currentTimeMillis() / 1000 / 30;
            return generateCode(secretBase32, currentTimeStep);
        } catch (Exception e) {
            System.err.println("Error generating current TOTP code: " + e.getMessage());
            return "000000";
        }
    }
    
    private String generateCode(String secretBase32, long timeStep) {
        try {
            // Decode base32 secret
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secretBase32);
            
            // Create HMAC-SHA1
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA1");
            mac.init(secretKeySpec);
            
            // Create message (time step as 8-byte big-endian)
            byte[] message = new byte[8];
            for (int i = 7; i >= 0; i--) {
                message[i] = (byte) (timeStep & 0xff);
                timeStep >>= 8;
            }
            
            // Generate HMAC
            byte[] hash = mac.doFinal(message);
            
            // Extract 4-byte dynamic binary code
            int offset = hash[hash.length - 1] & 0xf;
            int binary = ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
            
            // Generate 6-digit code
            int totpCode = binary % 1000000;
            
            return String.format("%06d", totpCode);
        } catch (Exception e) {
            return "000000";
        }
    }
}


