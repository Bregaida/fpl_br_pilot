package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Notam;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ControladorAisTest {

    @InjectMock
    PortaAisweb porta;

    @Test
    void deveRetornarNotams() {
        Mockito.when(porta.listarNotams("SBSP"))
                .thenReturn(List.of(new Notam("1", "NOTAM TESTE")));

        given()
            .when().get("/api/v1/ais/SBSP/notams")
            .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].id", equalTo("1"))
            .body("[0].texto", containsString("NOTAM"));
    }
}
