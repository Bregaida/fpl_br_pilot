package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.PubAixmDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.PubAixmParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Service para buscar dados AIXM do AISWEB.
 */
@ApplicationScoped
public class PubAixmService {

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    PubAixmParser pubAixmParser;

    /**
     * Lista dados AIXM.
     * 
     * @return Dados AIXM
     */
    public PubAixmDto listar() {
        try {
            Response response = aiswebClient.getPubAixmData(apiKey, apiPass, "pub", "AIXM");
            
            if (response.getStatus() != 200) {
                throw new RuntimeException("Erro ao buscar dados AIXM: " + response.getStatus());
            }
            
            String xml = response.readEntity(String.class);
            return pubAixmParser.parse(xml);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar dados AIXM", e);
        }
    }
}
