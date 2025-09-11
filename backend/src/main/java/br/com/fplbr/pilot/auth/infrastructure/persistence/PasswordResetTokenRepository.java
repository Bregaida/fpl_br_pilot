package br.com.fplbr.pilot.auth.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PasswordResetTokenRepository implements PanacheRepositoryBase<PasswordResetTokenEntity, UUID> {
    public Optional<PasswordResetTokenEntity> findValidByUser(UUID userId) {
        return find("userId = ?1 and used = false and expiresAt > now()", userId).firstResultOptional();
    }
}


