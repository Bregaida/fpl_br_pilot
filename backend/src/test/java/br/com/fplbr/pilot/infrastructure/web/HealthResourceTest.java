package br.com.fplbr.pilot.infrastructure.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class HealthResourceTest {

    @Test
    void health_shouldReturnUp() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/health")
        .then()
            .statusCode(200)
            .body(containsString("UP"));
    }
}
