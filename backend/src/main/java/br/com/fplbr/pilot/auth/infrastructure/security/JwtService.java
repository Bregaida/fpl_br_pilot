package br.com.fplbr.pilot.auth.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    public String issueAccessToken(String subject, Set<String> roles) {
        return Jwt.issuer("fplbr")
                .upn(subject)
                .groups(roles)
                .expiresIn(Duration.ofMinutes(10))
                .sign();
    }

    public String issueResetToken(String subject) {
        return Jwt.issuer("fplbr-reset")
                .upn(subject)
                .groups(Set.of("reset"))
                .expiresIn(Duration.ofMinutes(15))
                .sign();
    }
}


