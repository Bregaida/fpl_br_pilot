package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.PubAipDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.PubAipParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Service para buscar dados AIP do AISWEB.
 */
@ApplicationScoped
public class PubAipService {

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    PubAipParser pubAipParser;

    /**
     * Lista dados AIP.
     * 
     * @return Dados AIP
     */
    public PubAipDto listar() {
        try {
            Response response = aiswebClient.getPubAipData(apiKey, apiPass, "pub", "AIP");
            
            if (response.getStatus() != 200) {
                throw new RuntimeException("Erro ao buscar dados AIP: " + response.getStatus());
            }
            
            String xml = response.readEntity(String.class);
            return pubAipParser.parse(xml);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar dados AIP", e);
        }
    }
}
