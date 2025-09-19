package br.com.fplbr.pilot.auth.domain.model;

import java.time.*;
import java.util.UUID;
import java.util.Objects;

public class User {
    private UUID id;
    private String fullName;
    private String email;
    private String cpf;
    private String phoneDdd;
    private String phoneNumber;
    private String cep;
    private String address;
    private String addressNumber;
    private String addressComplement;
    private String neighborhood;
    private String city;
    private String uf;
    private LocalDate birthDate;
    private String maritalStatus;
    private String pilotType;
    private String company;
    private String loginAlias;
    private String passwordHash;
    private String passwordPepperVer;
    private boolean twoFactorEnabled;
    private String totpSecretEncrypted;
    private Long totpLastUsedCounter;
    private OffsetDateTime twoFactorVerifiedAt;
    private int failedLoginAttempts;
    private OffsetDateTime lockedUntil;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastLoginAt;
    private String consentsJson;
    private int auditVersion;

    // Construtor padrão
    public User() {}

    // Getters e Setters básicos
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTotpSecretEncrypted() { return totpSecretEncrypted; }
    public void setTotpSecretEncrypted(String totpSecretEncrypted) { this.totpSecretEncrypted = totpSecretEncrypted; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private UUID id;
        private String fullName;
        private String email;
        private String cpf;
        private String passwordHash;
        private String totpSecretEncrypted;
        private boolean twoFactorEnabled;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public UserBuilder id(UUID id) { this.id = id; return this; }
        public UserBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder cpf(String cpf) { this.cpf = cpf; return this; }
        public UserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UserBuilder totpSecretEncrypted(String totpSecretEncrypted) { this.totpSecretEncrypted = totpSecretEncrypted; return this; }
        public UserBuilder twoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; return this; }
        public UserBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.fullName = this.fullName;
            user.email = this.email;
            user.cpf = this.cpf;
            user.passwordHash = this.passwordHash;
            user.totpSecretEncrypted = this.totpSecretEncrypted;
            user.twoFactorEnabled = this.twoFactorEnabled;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            return user;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


