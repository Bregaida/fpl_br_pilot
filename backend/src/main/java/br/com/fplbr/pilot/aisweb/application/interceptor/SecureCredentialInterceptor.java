package br.com.fplbr.pilot.aisweb.application.interceptor;

import br.com.fplbr.pilot.aisweb.application.service.SecureCredentialService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class SecureCredentialInterceptor implements ClientRequestFilter {
    
    private static final Logger LOG = Logger.getLogger(SecureCredentialInterceptor.class);
    
    @Inject
    SecureCredentialService credentialService;
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        try {
            String apiKey = credentialService.getAiswebApiKey();
            String apiPass = credentialService.getAiswebApiPass();
            
            if (apiKey == null || apiPass == null) {
                LOG.warn("Credenciais do AISWEB não configuradas");
                return;
            }
            
            // Adicionar credenciais à URL
            URI originalUri = requestContext.getUri();
            URI uriWithCredentials = UriBuilder.fromUri(originalUri)
                .queryParam("apiKey", apiKey)
                .queryParam("apiPass", apiPass)
                .build();
            
            requestContext.setUri(uriWithCredentials);
            
            LOG.debug("Credenciais do AISWEB injetadas na requisição");
            
        } catch (Exception e) {
            LOG.error("Erro ao injetar credenciais do AISWEB", e);
        }
    }
}
