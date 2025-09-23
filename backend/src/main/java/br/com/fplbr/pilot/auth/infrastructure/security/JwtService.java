package br.com.fplbr.pilot.auth.infrastructure.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    
    @Inject
    JwtUtils jwtUtils;
    
    public String issueAccessToken(String subject, Set<String> roles) {
        // Temporary implementation using JwtUtils
        return jwtUtils.generateAccessToken(subject, roles);
    }

    public String issueResetToken(String subject) {
        // Temporary implementation using JwtUtils
        return jwtUtils.generateToken(subject, Set.of("reset"), 15, "reset");
    }
}


