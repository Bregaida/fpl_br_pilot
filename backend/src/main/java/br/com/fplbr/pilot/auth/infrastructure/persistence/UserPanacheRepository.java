package br.com.fplbr.pilot.auth.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserPanacheRepository implements PanacheRepositoryBase<UserEntity, UUID> {

    public Optional<UserEntity> findByEmailOrCpfOrAlias(String login) {
        String normalized = login.trim().toLowerCase();
        return find("lower(email) = ?1 or cpf = ?2 or lower(loginAlias) = ?3", normalized, normalized.replaceAll("[^0-9]", ""), normalized)
                .firstResultOptional();
    }
}


