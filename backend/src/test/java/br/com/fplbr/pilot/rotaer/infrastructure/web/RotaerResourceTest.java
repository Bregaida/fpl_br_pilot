package br.com.fplbr.pilot.rotaer.infrastructure.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para RotaerResource
 */
@QuarkusTest
public class RotaerResourceTest {
    
    @Test
    public void testHealthEndpoint() {
        given()
            .when().get("/api/v1/rotaer/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("service", equalTo("ROTAER"));
    }
    
    @Test
    public void testStatsEndpoint() {
        given()
            .when().get("/api/v1/rotaer/stats")
            .then()
            .statusCode(200)
            .body("total_aerodromos", notNullValue());
    }
    
    @Test
    public void testIngestEndpoint_MissingIcao() {
        String requestBody = """
            {
                "conteudo": "SBGL GALEÃO\\n1 GALEÃO\\n2 RIO DE JANEIRO\\n3 RJ"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when().post("/api/v1/rotaer/ingest")
            .then()
            .statusCode(400)
            .body("erro", equalTo("ICAO é obrigatório"));
    }
    
    @Test
    public void testIngestEndpoint_MissingContent() {
        String requestBody = """
            {
                "icao": "SBGL"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when().post("/api/v1/rotaer/ingest")
            .then()
            .statusCode(400)
            .body("erro", equalTo("Conteúdo do ROTAER é obrigatório"));
    }
    
    @Test
    public void testIngestEndpoint_DryRun() {
        String requestBody = """
            {
                "icao": "SBGL",
                "conteudo": "SBGL GALEÃO\\n1 GALEÃO\\n2 RIO DE JANEIRO\\n3 RJ\\n4 AD\\n5 INTL\\n6 PUB\\n7 INFRAERO\\n8 15 KM N DE RIO DE JANEIRO\\n9 UTC-3\\n10 VFR IFR\\n11 L21\\n12 Observações\\n13 FIR\\n14 Jurisdição"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .queryParam("dryRun", true)
            .when().post("/api/v1/rotaer/ingest")
            .then()
            .statusCode(200)
            .body("ok", equalTo(true))
            .body("icao", equalTo("SBGL"))
            .body("dryRun", equalTo(true))
            .body("warnings", notNullValue())
            .body("json", notNullValue());
    }
    
    @Test
    public void testValidateEndpoint() {
        String requestBody = """
            {
                "icao": "SBGL",
                "conteudo": "SBGL GALEÃO\\n1 GALEÃO\\n2 RIO DE JANEIRO\\n3 RJ\\n4 AD\\n5 INTL\\n6 PUB\\n7 INFRAERO\\n8 15 KM N DE RIO DE JANEIRO\\n9 UTC-3\\n10 VFR IFR\\n11 L21\\n12 Observações\\n13 FIR\\n14 Jurisdição"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when().post("/api/v1/rotaer/validate")
            .then()
            .statusCode(200)
            .body("ok", equalTo(true))
            .body("icao", equalTo("SBGL"))
            .body("warnings", notNullValue())
            .body("estatisticas", notNullValue());
    }
    
    @Test
    public void testBatchIngestEndpoint() {
        String requestBody = """
            {
                "aerodromos": {
                    "SBGL": "SBGL GALEÃO\\n1 GALEÃO\\n2 RIO DE JANEIRO\\n3 RJ\\n4 AD\\n5 INTL\\n6 PUB\\n7 INFRAERO\\n8 15 KM N DE RIO DE JANEIRO\\n9 UTC-3\\n10 VFR IFR\\n11 L21\\n12 Observações\\n13 FIR\\n14 Jurisdição",
                    "SBSP": "SBSP CONGONHAS\\n1 CONGONHAS\\n2 SÃO PAULO\\n3 SP\\n4 AD\\n5 INTL\\n6 PUB\\n7 INFRAERO\\n8 8 KM S DE SÃO PAULO\\n9 UTC-3\\n10 VFR IFR\\n11 L21\\n12 Observações\\n13 FIR\\n14 Jurisdição"
                }
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .queryParam("dryRun", true)
            .when().post("/api/v1/rotaer/ingest/batch")
            .then()
            .statusCode(200)
            .body("ok", equalTo(true))
            .body("dryRun", equalTo(true))
            .body("total", equalTo(2))
            .body("resultados", notNullValue());
    }
}
