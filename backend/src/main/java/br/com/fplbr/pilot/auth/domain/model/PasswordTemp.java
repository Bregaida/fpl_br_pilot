package br.com.fplbr.pilot.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Objects;

public class PasswordTemp {
    private UUID id;
    private UUID userId;
    private String tempHash;
    private OffsetDateTime expiresAt;
    private boolean used;
    
    // Construtor padrão
    public PasswordTemp() {}
    
    // Construtor com parâmetros
    public PasswordTemp(UUID id, UUID userId, String tempHash, OffsetDateTime expiresAt, boolean used) {
        this.id = id;
        this.userId = userId;
        this.tempHash = tempHash;
        this.expiresAt = expiresAt;
        this.used = used;
    }
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getTempHash() { return tempHash; }
    public void setTempHash(String tempHash) { this.tempHash = tempHash; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    
    // Builder
    public static PasswordTempBuilder builder() {
        return new PasswordTempBuilder();
    }
    
    public static class PasswordTempBuilder {
        private UUID id;
        private UUID userId;
        private String tempHash;
        private OffsetDateTime expiresAt;
        private boolean used;
        
        public PasswordTempBuilder id(UUID id) { this.id = id; return this; }
        public PasswordTempBuilder userId(UUID userId) { this.userId = userId; return this; }
        public PasswordTempBuilder tempHash(String tempHash) { this.tempHash = tempHash; return this; }
        public PasswordTempBuilder expiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public PasswordTempBuilder used(boolean used) { this.used = used; return this; }
        
        public PasswordTemp build() {
            return new PasswordTemp(id, userId, tempHash, expiresAt, used);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordTemp that = (PasswordTemp) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PasswordTemp{" +
                "id=" + id +
                ", userId=" + userId +
                ", tempHash='" + tempHash + '\'' +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}


