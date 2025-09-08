package br.com.fplbr.pilot.smoke;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class FlightplanIT {
  @Test void createAndFetch() {
    String payload = """
    {
      "modo":"PVC",
      "campo7":{"identificacaoAeronave":"PT-ABC","indicativoChamada":false},
      "campo8":{"regrasVoo":"V","tipoVoo":"G"},
      "campo9":{"numero":1,"tipoAeronave":"DECA","catTurbulencia":"L"},
      "campo13":{"aerodromoPartida":"SBSP","hora":"1230"},
      "campo15":{"velocidadeCruzeiro":"N0120","nivel":"A055","rota":"SBSP DCT SBMT"},
      "campo16":{"aerodromoDestino":"SBMT","eetTotal":"0030"},
      "campo18":{"DOF":"250829"},
      "campo19":{"autonomia":"0200","pessoasBordo":1,"pic":"EDUARDO","codAnac1":"123456","telefone":"+55XXXXXXXXX","corMarca":"Branco/Vermelho"}
    }
    """;
    String location =
      given().contentType(ContentType.JSON).body(payload)
        .when().post("/api/v1/flightplans")
        .then().statusCode(anyOf(is(201), is(200)))
        .extract().header("Location");
    if (location != null && !location.isBlank()) {
      given().when().get(location).then().statusCode(anyOf(is(200), is(302), is(303)));
    }
  }
}
