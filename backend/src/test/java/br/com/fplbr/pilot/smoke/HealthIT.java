package br.com.fplbr.pilot.smoke;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class HealthIT {
  @Test void shouldBeUp() {
    given().when().get("/q/health").then().statusCode(200).body("status", is("UP"));
  }
}
