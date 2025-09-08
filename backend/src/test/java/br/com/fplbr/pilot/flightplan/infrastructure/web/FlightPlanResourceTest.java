package br.com.fplbr.pilot.flightplan.infrastructure.web;

import br.com.fplbr.pilot.flightplan.application.service.FlightPlanService;
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
    void create_shouldReturn201_or200_andOptionallyLocation() {
        String payload = """
        {
          "identificacaoDaAeronave":"PTABC",
          "regraDeVooEnum":"VFR",
          "tipoDeVooEnum":"GERAL",
          "numeroDeAeronaves":1,
          "tipoDeAeronave":"C172",
          "categoriaEsteiraTurbulenciaEnum":"LEVE",
          "equipamentoCapacidadeDaAeronave": { "s": true },
          "vigilancia": { "s": true },
          "aerodromoDePartida":"SBSP",
          "horaPartida":"2030-12-31T10:00:00",
          "aerodromoDeDestino":"SBRJ",
          "tempoDeVooPrevisto":"2030-12-31T12:00:00",
          "aerodromoDeAlternativa":"SBGL",
          "velocidadeDeCruzeiro":"N0120",
          "nivelDeVoo":"F100",
          "rota":"DCT",
          "outrasInformacoes": { "rmk": "TEST" },
          "informacaoSuplementar": { "pilotoEmComando": "TEST" }
        }
        """;

        when(flightPlanService.createFlightPlan(any())).thenAnswer(inv -> inv.getArgument(0));

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/api/v1/flightplans")
        .then()
            .statusCode(anyOf(is(201), is(200)));
    }
}
