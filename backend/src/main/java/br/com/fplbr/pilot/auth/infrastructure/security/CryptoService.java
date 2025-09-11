package br.com.fplbr.pilot.auth.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class CryptoService {
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    @Inject
    SecurityConfig config;

    public String encrypt(String plaintext) {
        try {
            byte[] key = config.getTotpCryptoKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(normalizeKey(key), "AES");
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criptografar", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] input = Base64.getDecoder().decode(encoded);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input);
            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            byte[] key = config.getTotpCryptoKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(normalizeKey(key), "AES");
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao descriptografar", e);
        }
    }

    private byte[] normalizeKey(byte[] key) {
        byte[] out = new byte[16]; // 128-bit default; ensure strong key via env
        System.arraycopy(key, 0, out, 0, Math.min(key.length, out.length));
        return out;
    }
}


