package br.com.fplbr.pilot.auth.domain.model;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    private UUID id;
    private UUID userId;
    private String tokenHash;
    private OffsetDateTime expiresAt;
    private boolean used;
    private String ipFingerprint;
}


