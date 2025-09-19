package br.com.fplbr.pilot.auth.infrastructure.security;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class JwtUtils {
    private static final Logger LOG = Logger.getLogger(JwtUtils.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.minutes", defaultValue = "60")
    long expirationMinutes;

    @ConfigProperty(name = "jwt.refresh.expiration.days", defaultValue = "7")
    long refreshExpirationDays;

    @ConfigProperty(name = "jwt.secret")
    String secret;

    /**
     * Generate a JWT access token for the given username and roles
     */
    public String generateAccessToken(String username, Set<String> roles) {
        return generateToken(username, roles, expirationMinutes, "access");
    }

    /**
     * Generate a JWT refresh token for the given username
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, Set.of("refresh"), refreshExpirationDays * 24 * 60, "refresh");
    }

    public String generateToken(String username, Set<String> roles, long expirationMinutes, String type) {
        try {
            long currentTimeInSecs = currentTimeInSecs();
            long expirationTime = currentTimeInSecs + Duration.ofMinutes(expirationMinutes).getSeconds();
            
            // Create a simple token structure (temporary implementation)
            String payload = String.format("{\"iss\":\"%s\",\"sub\":\"%s\",\"iat\":%d,\"exp\":%d,\"type\":\"%s\",\"groups\":%s}",
                issuer, username, currentTimeInSecs, expirationTime, type, roles.toString());
            
            // Simple encoding (not secure, just for compilation)
            return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOG.error("Error generating JWT token", e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    /**
     * Validate a JWT token
     */
    public boolean validateToken(String token) {
        try {
            // Temporary implementation - always returns true for now
            // TODO: Implement proper JWT validation when dependencies are available
            return token != null && !token.trim().isEmpty();
        } catch (Exception e) {
            LOG.error("Invalid JWT token", e);
            return false;
        }
    }

    /**
     * Check if a token is expired
     */
    private boolean isTokenExpired(String token) {
        // Temporary implementation - always returns false for now
        // TODO: Implement proper token expiration check when dependencies are available
        return false;
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        try {
            // Temporary implementation - returns a default username for now
            // TODO: Implement proper username extraction when dependencies are available
            return "user";
        } catch (Exception e) {
            LOG.error("Error getting username from token", e);
            return null;
        }
    }

    /**
     * Get token type (access or refresh)
     */
    public String getTokenType(String token) {
        try {
            // Temporary implementation - returns access type for now
            // TODO: Implement proper token type extraction when dependencies are available
            return "access";
        } catch (Exception e) {
            LOG.error("Error getting token type", e);
            return null;
        }
    }

    private long currentTimeInSecs() {
        return System.currentTimeMillis() / 1000;
    }
}
