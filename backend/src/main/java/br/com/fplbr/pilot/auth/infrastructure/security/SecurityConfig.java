package br.com.fplbr.pilot.auth.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecurityConfig {

    @Inject
    @ConfigProperty(name = "app.security.password.pepper", defaultValue = "dev-pepper")
    String passwordPepper;

    @Inject
    @ConfigProperty(name = "app.security.totp.crypto.key", defaultValue = "dev-totp-crypto-key-1234")
    String totpCryptoKey;

    public String getPasswordPepper() {
        return passwordPepper != null ? passwordPepper : "";
    }

    public String getTotpCryptoKey() {
        return totpCryptoKey;
    }
}


