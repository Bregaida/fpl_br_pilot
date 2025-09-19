package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.SuplementosParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Service para buscar dados de suplementos do AISWEB.
 */
@ApplicationScoped
public class SuplementosService {

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    SuplementosParser suplementosParser;

    /**
     * Busca dados de suplementos para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados de suplementos do aeródromo
     */
    public SuplementosDto buscar(String icao) {
        try {
            Response response = aiswebClient.getSuplementosData(apiKey, apiPass, "suplementos", icao);
            
            if (response.getStatus() != 200) {
                throw new RuntimeException("Erro ao buscar dados de suplementos: " + response.getStatus());
            }
            
            String xml = response.readEntity(String.class);
            return suplementosParser.parse(xml);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar dados de suplementos para " + icao, e);
        }
    }
}
