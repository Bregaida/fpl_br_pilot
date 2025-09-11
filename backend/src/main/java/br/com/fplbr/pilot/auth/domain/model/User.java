package br.com.fplbr.pilot.auth.domain.model;

import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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
}


