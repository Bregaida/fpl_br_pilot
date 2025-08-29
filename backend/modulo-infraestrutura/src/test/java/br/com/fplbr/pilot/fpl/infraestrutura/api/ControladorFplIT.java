package br.com.fplbr.pilot.fpl.infraestrutura.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ControladorFplIT {

    @Test
    void preview_deveRetornarMensagemFpl_paraEntradaValida() {
        String json = """
        {
          "identificacaoAeronave": "PTABC",
          "regrasDeVoo": "IFR",
          "tipoDeVoo": "GERAL",
          "quantidadeAeronaves": 1,
          "tipoAeronaveIcao": "C172",
          "esteira": "L",
          "equipamentos": "SDFGRY/S",
          "aerodromoPartida": "SBSP",
          "horaPartidaZulu": "1300",
          "velocidadeCruzeiro": "N0100",
          "nivelCruzeiro": "F090",
          "rota": "DCT UZ31 PAB DCT",
          "aerodromoDestino": "SBRJ",
          "eetTotal": "0105",
          "alternativo1": "SBJR",
          "alternativo2": "SBRP",
          "outrosDados": "DOF/20250829 RMK/ENS",
          "autonomia": "0245",
          "pessoasABordo": 4,
          "piloto": "Bregaida"
        }
        """;

        given()
            .contentType("application/json")
            .body(json)
        .when()
            .post("/api/v1/fpl/preview")
        .then()
            .statusCode(200)
            .body("mensagemFpl", notNullValue())
            .body("erros", anyOf(nullValue(), hasSize(0)))
            .body("errosDetalhados", anyOf(nullValue(), hasSize(0)));
    }

    @Test
    void preview_deveRetornarErros_paraEntradaInvalida() {
        String json = """
        {
          "identificacaoAeronave": "PTDEF",
          "regrasDeVoo": "IFR",
          "tipoDeVoo": "GERAL",
          "quantidadeAeronaves": 0,
          "tipoAeronaveIcao": "C1",
          "esteira": "M",
          "equipamentos": "",
          "aerodromoPartida": "XXX",
          "horaPartidaZulu": "2460",
          "velocidadeCruzeiro": "BAD",
          "nivelCruzeiro": "Z999",
          "rota": "",
          "aerodromoDestino": "YYY",
          "eetTotal": "9999",
          "alternativo1": "ZZZ",
          "alternativo2": "Z1Z1",
          "outrosDados": "INVALIDO",
          "autonomia": "abcd",
          "pessoasABordo": -1,
          "piloto": ""
        }
        """;

        given()
            .contentType("application/json")
            .body(json)
        .when()
            .post("/api/v1/fpl/preview")
        .then()
            .statusCode(200)
            .body("mensagemFpl", nullValue())
            .body("erros", not(empty()))
            .body("errosDetalhados", not(empty()))
            .body("errosDetalhados[0].codigo", notNullValue())
            .body("errosDetalhados[0].mensagem", notNullValue());
    }
}
