package br.com.fplbr.pilot.smoke;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
class AerodromosIT {
  @Test void shouldSearch() {
    given().queryParam("query","SBMT").when().get("/api/v1/aerodromos")
      .then().statusCode(anyOf(is(200), is(204)));
  }
}
