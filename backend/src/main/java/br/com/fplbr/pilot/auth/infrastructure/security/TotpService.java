package br.com.fplbr.pilot.auth.infrastructure.security;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class TotpService {
    private static final Duration STEP = Duration.ofSeconds(30);

    @Inject
    CryptoService crypto;

    public String generateBase32Secret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA1");
            keyGenerator.init(160);
            SecretKey secretKey = keyGenerator.generateKey();
            return new Base32().encodeAsString(secretKey.getEncoded()).replace("=", "");
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar secret TOTP", e);
        }
    }

    public String buildOtpAuthUrl(String secretBase32, String issuer, String labelEmail) {
        String label = URLEncoder.encode(labelEmail, StandardCharsets.UTF_8);
        String iss = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        return "otpauth://totp/" + iss + ":" + label + "?secret=" + secretBase32 + "&issuer=" + iss + "&period=30&digits=6";
    }

    public boolean verify(String secretBase32, int code, long lastCounter) {
        try {
            byte[] keyBytes = new Base32().decode(secretBase32);
            Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA1");
            TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(STEP);
            Instant now = Instant.now();
            for (int i = -1; i <= 1; i++) {
                Instant when = now.plus(STEP.multipliedBy(i));
                int expected = totp.generateOneTimePassword(key, when);
                long counter = when.getEpochSecond() / STEP.getSeconds();
                if (expected == code && counter > lastCounter) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String encryptSecret(String base32) {
        return crypto.encrypt(base32);
    }

    public String decryptSecret(String encrypted) {
        return crypto.decrypt(encrypted);
    }
}


