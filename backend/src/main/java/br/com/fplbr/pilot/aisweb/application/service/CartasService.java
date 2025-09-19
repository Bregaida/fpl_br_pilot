package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.CartasDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.CartasParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service para buscar dados de cartas aeronáuticas do AISWEB.
 */
@ApplicationScoped
public class CartasService {
    private static final Logger LOG = Logger.getLogger(CartasService.class);
    
    private static final String API_SERVICE = "cartas";

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    CartasParser cartasParser;

    /**
     * Busca dados de cartas para um aeródromo específico.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Dados de cartas do aeródromo
     */
    /**
     * Busca dados de cartas aeronáuticas para um aeródromo específico.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return DTO contendo as cartas aeronáuticas
     * @throws WebApplicationException em caso de erro na requisição ou validação
     */
    public CartasDto buscar(String icao) {
        LOG.infof("Iniciando busca de cartas para o aeródromo: %s", icao);
        
        // Validação de entrada
        if (icao == null || icao.trim().length() != 4) {
            String errorMsg = "Código ICAO inválido. Deve conter 4 caracteres.";
            LOG.error(errorMsg);
            throw new WebApplicationException(errorMsg, Response.Status.BAD_REQUEST);
        }
        
        icao = icao.toUpperCase().trim();
        
        try {
            LOG.debugf("Solicitando dados de cartas para %s da API AISWEB", icao);
            Response response = aiswebClient.getCartasData(apiKey, apiPass, "cartas", icao);
            
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMsg = String.format("Erro ao buscar dados de cartas. Status: %d - %s", 
                    response.getStatus(), response.getStatusInfo().getReasonPhrase());
                LOG.error(errorMsg);
                throw new WebApplicationException(errorMsg, response.getStatus());
            }
            
            try {
                CartasDto cartasDto = cartasParser.parse(response.readEntity(String.class));
                LOG.debugf("Dados de cartas para %s processados com sucesso", icao);
                return cartasDto;
            } catch (Exception e) {
                String errorMsg = String.format("Erro ao processar resposta da API para %s: %s", icao, e.getMessage());
                LOG.error(errorMsg, e);
                throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
            }
            
        } catch (WebApplicationException e) {
            // Re-throw WebApplicationException as is
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao buscar cartas para %s: %s", icao, e.getMessage());
            LOG.error(errorMsg, e);
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            LOG.infof("Finalizada busca de cartas para o aeródromo: %s", icao);
        }
    }
}
