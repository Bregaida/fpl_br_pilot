package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.NotamDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.NotamParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.ws.rs.core.Response;

/**
 * Service para buscar dados NOTAM do AISWEB.
 */
@ApplicationScoped
public class NotamService {

    @Inject
    @RestClient
    AiswebClient aiswebClient;
    
    @Inject
    NotamParser notamParser;
    
    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;
    
    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    /**
     * Busca dados NOTAM para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados NOTAM do aeródromo
     */
    public NotamDto buscar(String icao) {
        try {
            Response response = aiswebClient.getNotamData(apiKey, apiPass, "notam", icao);
            
            if (response.getStatus() != 200) {
                throw new RuntimeException("Erro ao buscar dados NOTAM: " + response.getStatus());
            }
            
            String xml = response.readEntity(String.class);
            return notamParser.parse(xml);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar dados NOTAM para " + icao, e);
        }
    }
}
