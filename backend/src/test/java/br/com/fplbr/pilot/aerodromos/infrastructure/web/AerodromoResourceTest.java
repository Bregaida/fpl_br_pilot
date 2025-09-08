package br.com.fplbr.pilot.aerodromos.infrastructure.web;

import br.com.fplbr.pilot.aerodromos.application.dto.AerodromoDTO;
import br.com.fplbr.pilot.aerodromos.application.service.AerodromoService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
class AerodromoResourceTest {

    @InjectMock
    AerodromoService aerodromoService;

    @Test
    void listar_shouldReturn200() {
        var dto = AerodromoDTO.builder().icao("SBSP").nomeOficial("Congonhas").uf("SP").build();
        when(aerodromoService.buscarAerodromos("SBSP", null, 0, 20)).thenReturn(List.of(dto));

        given()
            .accept(ContentType.JSON)
            .queryParam("query", "SBSP")
        .when()
            .get("/api/v1/aerodromos")
        .then()
            .statusCode(200);
    }
}
