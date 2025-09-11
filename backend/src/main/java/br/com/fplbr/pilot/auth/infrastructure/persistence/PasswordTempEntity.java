package br.com.fplbr.pilot.auth.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_temp")
@Getter
@Setter
public class PasswordTempEntity extends PanacheEntityBase {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "temp_hash", length = 200, nullable = false)
    private String tempHash;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "used", nullable = false)
    private boolean used;
}


