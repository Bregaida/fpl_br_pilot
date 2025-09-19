package br.com.fplbr.pilot.auth.domain.model;

import java.util.Objects;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PasswordResetToken {
    private UUID id;
    private UUID userId;
    private String tokenHash;
    private OffsetDateTime expiresAt;
    private boolean used;
    private String ipFingerprint;
    
    // Construtor padrão
    public PasswordResetToken() {}
    
    // Getters e Setters básicos
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public String getIpFingerprint() { return ipFingerprint; }
    public void setIpFingerprint(String ipFingerprint) { this.ipFingerprint = ipFingerprint; }
    
    // Builder
    public static PasswordResetTokenBuilder builder() {
        return new PasswordResetTokenBuilder();
    }
    
    public static class PasswordResetTokenBuilder {
        private UUID id;
        private UUID userId;
        private String tokenHash;
        private OffsetDateTime expiresAt;
        private boolean used;
        private String ipFingerprint;
        
        public PasswordResetTokenBuilder id(UUID id) { this.id = id; return this; }
        public PasswordResetTokenBuilder userId(UUID userId) { this.userId = userId; return this; }
        public PasswordResetTokenBuilder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
        public PasswordResetTokenBuilder expiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public PasswordResetTokenBuilder used(boolean used) { this.used = used; return this; }
        public PasswordResetTokenBuilder ipFingerprint(String ipFingerprint) { this.ipFingerprint = ipFingerprint; return this; }
        
        public PasswordResetToken build() {
            PasswordResetToken token = new PasswordResetToken();
            token.id = this.id;
            token.userId = this.userId;
            token.tokenHash = this.tokenHash;
            token.expiresAt = this.expiresAt;
            token.used = this.used;
            token.ipFingerprint = this.ipFingerprint;
            return token;
        }
    }
}


