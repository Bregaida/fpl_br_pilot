package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.MeteoDto;
import br.com.fplbr.pilot.aisweb.application.dto.MetarDto;
import br.com.fplbr.pilot.aisweb.application.dto.TafDto;
import br.com.fplbr.pilot.aisweb.application.dto.SunDto;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.MetTafParser;
import br.com.fplbr.pilot.aisweb.infrastructure.parser.SunParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service para buscar dados meteorológicos do AISWEB.
 */
@ApplicationScoped
public class MeteoService {
    private static final Logger LOG = Logger.getLogger(MeteoService.class);

    @Inject
    @RestClient
    AiswebClient aiswebClient;

    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;

    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;

    @Inject
    MetTafParser metTafParser;

    @Inject
    SunParser sunParser;


    /**
     * Busca dados meteorológicos para um aeródromo específico.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return Dados meteorológicos do aeródromo
     * @throws WebApplicationException em caso de erro na requisição
     */
    public MeteoDto buscar(String icao) {
        if (icao == null || icao.trim().length() != 4) {
            throw new WebApplicationException("Código ICAO inválido. Deve conter 4 caracteres.",
                Response.Status.BAD_REQUEST);
        }

        icao = icao.toUpperCase();
        LOG.infof("Buscando dados meteorológicos para o aeródromo: %s", icao);

        try {
            // Buscar dados do sol (horário de nascer e pôr do sol)
            SunDto sol = buscarDadosSol(icao);

            // Buscar METAR (condições meteorológicas atuais)
            MetarDto metar = buscarMetar(icao);

            // Buscar TAF (previsão de tempo)
            TafDto taf = buscarTaf(icao);

            return new MeteoDto(icao, sol, metar, taf);

        } catch (WebApplicationException e) {
            LOG.errorf("Erro na requisição de dados meteorológicos para %s: %s", icao, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao buscar dados meteorológicos para %s: %s",
                icao, e.getMessage());
            LOG.error(errorMsg, e);
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Busca dados do sol (horário de nascer e pôr do sol) para um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados do sol
     */
    private SunDto buscarDadosSol(String icao) {
        try {
            Response response = aiswebClient.getSunData(apiKey, apiPass, "sol", icao);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String xml = response.readEntity(String.class);
                return sunParser.parse(xml);
            } else {
                LOG.warnf("Não foi possível obter dados do sol para %s. Status: %d",
                    icao, response.getStatus());
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("Erro ao buscar dados do sol para %s: %s", icao, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca METAR (condições meteorológicas atuais) para um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados METAR
     */
    private MetarDto buscarMetar(String icao) {
        try {
            Response response = aiswebClient.getMetarData(apiKey, apiPass, "met", icao);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String xml = response.readEntity(String.class);
                return metTafParser.parseMetar(xml);
            } else {
                LOG.warnf("Não foi possível obter METAR para %s. Status: %d",
                    icao, response.getStatus());
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("Erro ao buscar METAR para %s: %s", icao, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca TAF (previsão de tempo) para um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados TAF
     */
    private TafDto buscarTaf(String icao) {
        try {
            Response response = aiswebClient.getMetarData(apiKey, apiPass, "met", icao);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String xml = response.readEntity(String.class);
                return metTafParser.parseTaf(xml);
            } else {
                LOG.warnf("Não foi possível obter TAF para %s. Status: %d",
                    icao, response.getStatus());
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("Erro ao buscar TAF para %s: %s", icao, e.getMessage(), e);
            return null;
        }
    }


}
