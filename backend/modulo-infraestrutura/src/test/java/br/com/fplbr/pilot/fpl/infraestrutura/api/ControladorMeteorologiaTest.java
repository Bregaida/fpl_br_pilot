package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.dto.BriefingMeteorologico;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaMeteorologia;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ControladorMeteorologiaTest {

    @InjectMock
    PortaMeteorologia porta;

    @Test
    void deveRetornarBriefing() {
        Mockito.when(porta.obterBriefing("SBSP"))
                .thenReturn(new BriefingMeteorologico("METAR SBSP...", "TAF SBSP...", List.of("SIGMET")));

        given()
            .when().get("/api/v1/meteorologia/SBSP/briefing")
            .then()
            .statusCode(200)
            .body("metar", startsWith("METAR"))
            .body("taf", startsWith("TAF"))
            .body("sigmet.size()", is(1));
    }
}
