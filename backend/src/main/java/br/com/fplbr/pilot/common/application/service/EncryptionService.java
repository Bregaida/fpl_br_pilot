package br.com.fplbr.pilot.common.application.service;

import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class EncryptionService {
    
    private static final Logger LOG = Logger.getLogger(EncryptionService.class);
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    
    // Chave mestra do AES-GCM. Nao ha valor embutido: vem do ambiente, via a
    // variavel MASTER_ENCRYPTION_KEY, e deve ser Base64 de 32 bytes.
    // O defaultValue vazio existe para a aplicacao subir sem a variavel e
    // falhar com mensagem clara no primeiro uso, em vez de cifrar com uma
    // chave conhecida ou quebrar a injecao no boot.
    @ConfigProperty(name = "MASTER_ENCRYPTION_KEY", defaultValue = "")
    String masterKey;
    
    public EncryptionResult encrypt(byte[] plaintext) {
        try {
            if (masterKey == null || masterKey.isEmpty()) {
                throw new IllegalStateException("MASTER_ENCRYPTION_KEY não configurada");
            }
            
            // Gerar IV aleatório (12 bytes para GCM)
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            // Criar chave a partir da chave mestra (Base64)
            byte[] keyBytes = Base64.getDecoder().decode(masterKey);
            if (keyBytes.length != 32) {
                throw new IllegalStateException("MASTER_ENCRYPTION_KEY deve ser Base64 de 32 bytes");
            }
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // Criptografar com GCM
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            byte[] encrypted = cipher.doFinal(plaintext);
            
            return new EncryptionResult(iv, encrypted);
            
        } catch (Exception e) {
            LOG.error("Erro ao criptografar dados", e);
            throw new RuntimeException("Falha na criptografia", e);
        }
    }
    
    public String encrypt(String plainText) {
        EncryptionResult result = encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        // Para compatibilidade com código existente
        byte[] combined = new byte[result.iv.length + result.ciphertext.length];
        System.arraycopy(result.iv, 0, combined, 0, result.iv.length);
        System.arraycopy(result.ciphertext, 0, combined, result.iv.length, result.ciphertext.length);
        return Base64.getEncoder().encodeToString(combined);
    }
    
    public byte[] decrypt(byte[] iv, byte[] ciphertext) {
        try {
            if (masterKey == null || masterKey.isEmpty()) {
                throw new IllegalStateException("MASTER_ENCRYPTION_KEY não configurada");
            }
            
            // Criar chave a partir da chave mestra (Base64)
            byte[] keyBytes = Base64.getDecoder().decode(masterKey);
            if (keyBytes.length != 32) {
                throw new IllegalStateException("MASTER_ENCRYPTION_KEY deve ser Base64 de 32 bytes");
            }
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // Descriptografar com GCM
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            return cipher.doFinal(ciphertext);
            
        } catch (Exception e) {
            LOG.error("Erro ao descriptografar dados", e);
            throw new RuntimeException("Falha na descriptografia", e);
        }
    }
    
    public String decrypt(String encryptedText) {
        try {
            // Decodificar Base64
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // Separar IV e dados criptografados (compatibilidade com formato antigo)
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            byte[] decrypted = decrypt(iv, encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            LOG.error("Erro ao descriptografar dados", e);
            throw new RuntimeException("Falha na descriptografia", e);
        }
    }
    
    public String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    public static class EncryptionResult {
        public final byte[] iv;
        public final byte[] ciphertext;
        
        public EncryptionResult(byte[] iv, byte[] ciphertext) {
            this.iv = iv;
            this.ciphertext = ciphertext;
        }
    }
}
