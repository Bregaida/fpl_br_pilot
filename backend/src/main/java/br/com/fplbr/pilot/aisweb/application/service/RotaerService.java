package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.RotaerDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.RotaerParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service para buscar dados ROTAER do AISWEB.
 */
@ApplicationScoped
public class RotaerService {

    private static final Logger LOG = Logger.getLogger(RotaerService.class);

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    RotaerParser rotaerParser;

    /**
     * Busca dados ROTAER para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados ROTAER do aeródromo
     */
    public RotaerDto buscar(String icao) {
        try {
            LOG.infof("Buscando dados ROTAER para o aeródromo: %s", icao);
            Response response = aiswebClient.getRotaerData(apiKey, apiPass, "rotaer", icao);
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String xmlResponse = response.readEntity(String.class);
                return rotaerParser.parse(xmlResponse, icao);
            } else {
                String errorMsg = String.format("Erro ao buscar dados ROTAER para %s. Status: %d", 
                    icao, response.getStatus());
                LOG.error(errorMsg);
                throw new WebApplicationException(errorMsg, response.getStatus());
            }
            
        } catch (WebApplicationException e) {
            LOG.errorf("Erro na requisição ROTAER: %s", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao buscar dados ROTAER para %s: %s", icao, e.getMessage());
            LOG.error(errorMsg, e);
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
