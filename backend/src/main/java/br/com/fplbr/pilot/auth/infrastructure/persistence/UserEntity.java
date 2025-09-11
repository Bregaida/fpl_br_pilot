package br.com.fplbr.pilot.auth.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
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
}


