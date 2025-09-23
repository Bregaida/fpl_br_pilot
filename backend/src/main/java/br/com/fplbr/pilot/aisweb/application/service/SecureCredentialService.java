package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.common.application.service.EncryptionService;
import br.com.fplbr.pilot.common.domain.entity.EncryptedCredential;
import br.com.fplbr.pilot.common.infrastructure.persistence.CredentialRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ApplicationScoped
public class SecureCredentialService {
    
    private static final Logger LOG = Logger.getLogger(SecureCredentialService.class);
    
    @Inject
    CredentialRepository credentialRepository;
    
    @Inject
    EncryptionService encryptionService;
    
    @Inject
    CredentialInitializationService credentialInitializationService;
    
    private static final String AISWEB_SERVICE = "aisweb";
    
    public String getAiswebApiKey() {
        return credentialInitializationService.getApiKey();
    }
    
    public String getAiswebApiPass() {
        return credentialInitializationService.getApiPass();
    }
    
    public void setAiswebCredentials(String apiKey, String apiPass) {
        LOG.info("Criptografando e armazenando credenciais do AISWEB");
        
        // Serializar credenciais como JSON
        String credentialsJson = String.format("{\"apiKey\":\"%s\",\"apiPass\":\"%s\"}", apiKey, apiPass);
        
        // Criptografar com GCM
        EncryptionService.EncryptionResult result = encryptionService.encrypt(
            credentialsJson.getBytes(StandardCharsets.UTF_8)
        );
        
        credentialRepository.saveOrUpdate(AISWEB_SERVICE, result.iv, result.ciphertext);
        
        LOG.info("Credenciais do AISWEB armazenadas com segurança");
    }
    
    private AiswebCredentials getAiswebCredentials() {
        try {
            Optional<EncryptedCredential> credential = credentialRepository.findByService(AISWEB_SERVICE);
            
            if (credential.isEmpty()) {
                LOG.warn("Credenciais do AISWEB não encontradas");
                return null;
            }
            
            // Descriptografar
            byte[] decrypted = encryptionService.decrypt(
                credential.get().getIv(), 
                credential.get().getCiphertext()
            );
            
            String credentialsJson = new String(decrypted, StandardCharsets.UTF_8);
            
            // Parse simples do JSON (para evitar dependência externa)
            String apiKey = extractJsonValue(credentialsJson, "apiKey");
            String apiPass = extractJsonValue(credentialsJson, "apiPass");
            
            return new AiswebCredentials(apiKey, apiPass);
            
        } catch (Exception e) {
            LOG.error("Erro ao recuperar credenciais do AISWEB", e);
            return null;
        }
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }
    
    public boolean hasCredentials() {
        return getAiswebApiKey() != null && getAiswebApiPass() != null;
    }
    
    private static class AiswebCredentials {
        final String apiKey;
        final String apiPass;
        
        AiswebCredentials(String apiKey, String apiPass) {
            this.apiKey = apiKey;
            this.apiPass = apiPass;
        }
    }
}
