package br.com.fplbr.pilot.auth.infrastructure.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PasswordHasher {
    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    @Inject
    SecurityConfig config;

    public String hash(String raw) {
        String pepper = config.getPasswordPepper();
        // Parameters tuned for security; adjust for environment
        int iterations = 3;
        int memory = 1 << 15; // 32 MB
        int parallelism = 2;
        return argon2.hash(iterations, memory, parallelism, (raw + pepper).toCharArray());
    }

    public boolean verify(String hash, String raw) {
        String pepper = config.getPasswordPepper();
        return argon2.verify(hash, (raw + pepper).toCharArray());
    }
}


