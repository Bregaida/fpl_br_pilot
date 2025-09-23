package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.SunDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.SunParser;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service para buscar dados do sol do AISWEB.
 */
@ApplicationScoped
public class SunService {
    private static final Logger LOG = Logger.getLogger(SunService.class);

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @Inject
    SecureCredentialService credentialService;

    @Inject
    SunParser sunParser;

    /**
     * Busca dados do sol para um aeródromo específico.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return Dados do sol do aeródromo
     * @throws WebApplicationException em caso de erro na requisição
     */
    public SunDto buscar(String icao) {
        if (icao == null || icao.trim().length() != 4) {
            throw new WebApplicationException("Código ICAO inválido. Deve conter 4 caracteres.",
                Response.Status.BAD_REQUEST);
        }

        icao = icao.toUpperCase();
        LOG.infof("Buscando dados do sol para o aeródromo: %s", icao);

        try {
            String apiKey = credentialService.getAiswebApiKey();
            String apiPass = credentialService.getAiswebApiPass();
            Response response = aiswebClient.getSunData(apiKey, apiPass, "sol", icao);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String xml = response.readEntity(String.class);
                return sunParser.parse(xml);
            } else {
                String errorMsg = String.format("Erro ao buscar dados do sol para %s. Status: %d", 
                    icao, response.getStatus());
                LOG.error(errorMsg);
                throw new WebApplicationException(errorMsg, response.getStatus());
            }

        } catch (WebApplicationException e) {
            LOG.errorf("Erro na requisição de dados do sol para %s: %s", icao, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao buscar dados do sol para %s: %s", 
                icao, e.getMessage());
            LOG.error(errorMsg, e);
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
