package br.com.fplbr.pilot.auth.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "full_name", length = 32, nullable = false)
    private String fullName;
    @Column(name = "email", length = 40, nullable = false, unique = true)
    private String email;
    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String cpf;
    @Column(name = "phone_ddd", length = 2, nullable = false)
    private String phoneDdd;
    @Column(name = "phone_number", length = 9, nullable = false)
    private String phoneNumber;
    @Column(name = "cep", length = 8, nullable = false)
    private String cep;
    @Column(name = "address", length = 40, nullable = false)
    private String address;
    @Column(name = "address_number", length = 10, nullable = false)
    private String addressNumber;
    @Column(name = "address_complement", length = 20, nullable = false)
    private String addressComplement;
    @Column(name = "neighborhood", length = 40, nullable = false)
    private String neighborhood;
    @Column(name = "city", length = 40, nullable = false)
    private String city;
    @Column(name = "uf", length = 2, nullable = false)
    private String uf;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(name = "marital_status", length = 12, nullable = false)
    private String maritalStatus;
    @Column(name = "pilot_type", length = 20, nullable = false)
    private String pilotType;
    @Column(name = "company", length = 40)
    private String company;
    @Column(name = "login_alias", length = 40, nullable = false, unique = true)
    private String loginAlias;
    @Column(name = "password_hash", length = 200, nullable = false)
    private String passwordHash;
    @Column(name = "password_pepper_ver")
    private Integer passwordPepperVer;
    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled;
    @Column(name = "totp_secret_encrypted", length = 512)
    private String totpSecretEncrypted;
    @Column(name = "totp_last_used_counter")
    private Long totpLastUsedCounter;
    @Column(name = "two_factor_verified_at")
    private OffsetDateTime twoFactorVerifiedAt;
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;
    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;
    @Column(name = "consents_json")
    private String consentsJson;
    @Column(name = "audit_version", nullable = false)
    private int auditVersion;
    
    // === GETTERS ===
    
    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getPhoneDdd() { return phoneDdd; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCep() { return cep; }
    public String getAddress() { return address; }
    public String getAddressNumber() { return addressNumber; }
    public String getAddressComplement() { return addressComplement; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity() { return city; }
    public String getUf() { return uf; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getPilotType() { return pilotType; }
    public String getCompany() { return company; }
    public String getLoginAlias() { return loginAlias; }
    public String getPasswordHash() { return passwordHash; }
    public int getPasswordPepperVer() { return passwordPepperVer; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public String getTotpSecretEncrypted() { return totpSecretEncrypted; }
    public long getTotpLastUsedCounter() { return totpLastUsedCounter; }
    public OffsetDateTime getTwoFactorVerifiedAt() { return twoFactorVerifiedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getLastLoginAt() { return lastLoginAt; }
    public String getConsentsJson() { return consentsJson; }
    public int getAuditVersion() { return auditVersion; }
    
    // === SETTERS ===
    
    public void setId(UUID id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setPhoneDdd(String phoneDdd) { this.phoneDdd = phoneDdd; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setCep(String cep) { this.cep = cep; }
    public void setAddress(String address) { this.address = address; }
    public void setAddressNumber(String addressNumber) { this.addressNumber = addressNumber; }
    public void setAddressComplement(String addressComplement) { this.addressComplement = addressComplement; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setCity(String city) { this.city = city; }
    public void setUf(String uf) { this.uf = uf; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public void setPilotType(String pilotType) { this.pilotType = pilotType; }
    public void setCompany(String company) { this.company = company; }
    public void setLoginAlias(String loginAlias) { this.loginAlias = loginAlias; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setPasswordPepperVer(int passwordPepperVer) { this.passwordPepperVer = passwordPepperVer; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    public void setTotpSecretEncrypted(String totpSecretEncrypted) { this.totpSecretEncrypted = totpSecretEncrypted; }
    public void setTotpLastUsedCounter(long totpLastUsedCounter) { this.totpLastUsedCounter = totpLastUsedCounter; }
    public void setTwoFactorVerifiedAt(OffsetDateTime twoFactorVerifiedAt) { this.twoFactorVerifiedAt = twoFactorVerifiedAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setLastLoginAt(OffsetDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setConsentsJson(String consentsJson) { this.consentsJson = consentsJson; }
    public void setAuditVersion(int auditVersion) { this.auditVersion = auditVersion; }
    
    // Métodos faltantes para compilar
    public OffsetDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(OffsetDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
}


