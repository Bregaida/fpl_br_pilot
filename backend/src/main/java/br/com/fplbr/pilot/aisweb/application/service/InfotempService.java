package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.InfotempDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.InfotempParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Service para buscar dados INFOTEMP do AISWEB.
 */
@ApplicationScoped
public class InfotempService {

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    InfotempParser infotempParser;

    /**
     * Busca dados INFOTEMP para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados INFOTEMP do aeródromo
     */
    /**
     * Busca dados INFOTEMP para um aeródromo específico.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return Dados INFOTEMP do aeródromo
     * @throws jakarta.ws.rs.WebApplicationException em caso de erro na requisição
     */
    public InfotempDto buscar(String icao) {
        if (icao == null || icao.trim().length() != 4) {
            throw new jakarta.ws.rs.WebApplicationException(
                "Código ICAO inválido. Deve conter 4 caracteres.",
                jakarta.ws.rs.core.Response.Status.BAD_REQUEST
            );
        }

        icao = icao.toUpperCase();
        org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(getClass());
        logger.infof("Buscando dados INFOTEMP para o aeródromo: %s", icao);

        try {
            Response response = aiswebClient.getInfotempData(apiKey, apiPass, "infotemp", icao, "0");
            
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMsg = String.format("Erro ao buscar dados INFOTEMP para %s. Status: %d", 
                    icao, response.getStatus());
                logger.error(errorMsg);
                throw new jakarta.ws.rs.WebApplicationException(errorMsg, response.getStatus());
            }
            
            String xml = response.readEntity(String.class);
            logger.debugf("Resposta INFOTEMP para %s: %s", icao, xml);
            return infotempParser.parse(xml);
            
        } catch (jakarta.ws.rs.WebApplicationException e) {
            logger.errorf("Erro na requisição INFOTEMP: %s", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao buscar dados INFOTEMP para %s: %s", 
                icao, e.getMessage());
            logger.error(errorMsg, e);
            throw new jakarta.ws.rs.WebApplicationException(
                errorMsg, 
                jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
            );
        }
    }
}
