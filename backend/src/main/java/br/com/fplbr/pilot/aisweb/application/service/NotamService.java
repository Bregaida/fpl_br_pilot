package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.NotamDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.NotamParser;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import javax.ws.rs.core.Response;

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
    
    @Inject
    SecureCredentialService credentialService;

    /**
     * Busca dados NOTAM para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados NOTAM do aeródromo
     */
    public NotamDto buscar(String icao) {
        try {
            String apiKey = credentialService.getAiswebApiKey();
            String apiPass = credentialService.getAiswebApiPass();
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
