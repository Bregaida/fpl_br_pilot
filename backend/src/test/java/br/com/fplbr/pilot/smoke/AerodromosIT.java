package br.com.fplbr.pilot.smoke;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AerodromosIT {
  @Test void shouldSearch() {
    given().queryParam("query","SBSP").when().get("/api/v1/aerodromos")
      .then().statusCode(anyOf(is(200), is(204)));
  }
}
