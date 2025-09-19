package br.com.fplbr.pilot.auth.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class PasswordHasher {
    
    @Inject
    SecurityConfig config;

    public String hash(String raw) {
        try {
            String pepper = config.getPasswordPepper();
            String saltedPassword = raw + pepper;
            
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash with SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(saltedPassword.getBytes());
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public boolean verify(String hash, String raw) {
        try {
            String pepper = config.getPasswordPepper();
            String saltedPassword = raw + pepper;
            
            // Decode the stored hash
            byte[] combined = Base64.getDecoder().decode(hash);
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Extract stored hash (remaining bytes)
            byte[] storedHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedHash, 0, storedHash.length);
            
            // Hash the input password with the extracted salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(saltedPassword.getBytes());
            
            // Compare hashes
            return MessageDigest.isEqual(storedHash, hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}


