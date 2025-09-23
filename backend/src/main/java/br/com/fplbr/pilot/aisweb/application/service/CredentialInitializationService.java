package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.common.application.service.EncryptionService;
import br.com.fplbr.pilot.common.domain.entity.EncryptedCredential;
import br.com.fplbr.pilot.common.infrastructure.persistence.CredentialRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ApplicationScoped
public class CredentialInitializationService {
    
    private static final Logger LOG = Logger.getLogger(CredentialInitializationService.class);
    
    @Inject
    CredentialRepository credentialRepository;
    
    @Inject
    EncryptionService encryptionService;
    
    // Credenciais serão carregadas apenas do banco de dados
    
    private String decryptedApiKey;
    private String decryptedApiPass;
    
    @PostConstruct
    @Transactional
    public void initializeCredentials() {
        try {
            LOG.info("Inicializando credenciais do AISWEB...");
            
            // Verificar se já existem credenciais no banco
            Optional<EncryptedCredential> existingCredential = credentialRepository.findByService("aisweb");
            
            if (existingCredential.isEmpty()) {
                LOG.warn("Credenciais não encontradas no banco de dados!");
                LOG.warn("Use o endpoint /api/admin/credentials para configurar as credenciais do AISWEB");
                decryptedApiKey = null;
                decryptedApiPass = null;
            } else {
                LOG.info("Credenciais encontradas no banco de dados, carregando...");
                loadFromDatabase(existingCredential.get());
                LOG.info("Credenciais do AISWEB carregadas com sucesso!");
            }
            
        } catch (Exception e) {
            LOG.error("Erro ao inicializar credenciais do AISWEB", e);
            decryptedApiKey = null;
            decryptedApiPass = null;
        }
    }
    
    
    private void loadFromDatabase(EncryptedCredential credential) {
        try {
            byte[] decrypted = encryptionService.decrypt(
                credential.getIv(), 
                credential.getCiphertext()
            );
            
            String credentialsJson = new String(decrypted, StandardCharsets.UTF_8);
            
            // Parse do JSON
            decryptedApiKey = extractJsonValue(credentialsJson, "apiKey");
            decryptedApiPass = extractJsonValue(credentialsJson, "apiPass");
            
            LOG.info("Credenciais carregadas do banco de dados com sucesso");
            
        } catch (Exception e) {
            LOG.error("Erro ao descriptografar credenciais do banco", e);
            decryptedApiKey = null;
            decryptedApiPass = null;
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
    
    
    
    public String getApiKey() {
        return decryptedApiKey;
    }
    
    public String getApiPass() {
        return decryptedApiPass;
    }
    
    public boolean hasCredentials() {
        return decryptedApiKey != null && decryptedApiPass != null;
    }
}

