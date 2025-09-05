package br.com.fplbr.pilot.flightplan.infrastructure.web;

import br.com.fplbr.pilot.flightplan.application.service.FlightPlanService;
import br.com.fplbr.pilot.flightplan.infrastructure.web.dto.FlightPlanDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class FlightPlanResourceTest {

    @InjectMock
    FlightPlanService flightPlanService;

    @Test
    void create_shouldReturn201_or400() {
        FlightPlanDTO input = FlightPlanDTO.builder()
                .identificacaoDaAeronave("PTABC")
                .regraDeVooEnum(null)
                .tipoDeVooEnum(null)
                .equipamentoCapacidadeDaAeronave(null)
                .vigilancia(null)
                .aerodromoDePartida("SBSP")
                .horaPartida(null)
                .aerodromoDeDestino("SBRJ")
                .tempoDeVooPrevisto(null)
                .aerodromoDeAlternativa("SBGL")
                .velocidadeDeCruzeiro("N0120")
                .nivelDeVoo("F100")
                .rota("DCT")
                .outrasInformacoes(null)
                .informacaoSuplementar(null)
                .build();
        when(flightPlanService.createFlightPlan(any())).thenReturn(input);

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(input)
        .when()
            .post("/api/v1/flightplans")
        .then()
            .statusCode(anyOf(is(201), is(400)));
    }
}
