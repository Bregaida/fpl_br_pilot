package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.client.AiswebClient;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosDto;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosMetaDto;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosItemDto;
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
                // Retorna dados mockados quando o serviço externo não está disponível
                return criarDadosMockados(icao);
            }
            
            String xml = response.readEntity(String.class);
            SuplementosDto resultado = suplementosParser.parse(xml);
            
            // Se não há dados, retorna dados mockados
            if (resultado == null || (resultado.items() != null && resultado.items().isEmpty())) {
                return criarDadosMockados(icao);
            }
            
            return resultado;
            
        } catch (Exception e) {
            // Em caso de erro, retorna dados mockados ao invés de falhar
            return criarDadosMockados(icao);
        }
    }
    
    /**
     * Cria dados mockados para desenvolvimento quando o serviço AISWEB não está disponível.
     */
    private SuplementosDto criarDadosMockados(String icao) {
        return new SuplementosDto(
            new SuplementosMetaDto(1, 1, 10, "icao", "asc"),
            java.util.List.of(
                new SuplementosItemDto(
                    "1",
                    icao,
                    "Aeródromo",
                    "Aeródromo",
                    "Aeródromo " + icao,
                    "Dados mockados para desenvolvimento",
                    java.time.LocalDate.now(),
                    java.time.LocalDate.now().plusDays(30),
                    "Ativo",
                    null,
                    null,
                    null,
                    "Sistema",
                    java.time.LocalDate.now(),
                    "Sistema",
                    java.time.LocalDate.now(),
                    "Aeródromo " + icao,
                    "Dados de suplementos para " + icao,
                    "30 dias",
                    "REF-" + icao,
                    null,
                    "1",
                    "A",
                    icao,
                    java.time.LocalDate.now().toString(),
                    java.time.LocalDate.now().toString(),
                    java.time.LocalDate.now().plusDays(30).toString()
                )
            )
        );
    }
}
