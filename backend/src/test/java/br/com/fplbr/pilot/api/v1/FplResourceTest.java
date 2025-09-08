package br.com.fplbr.pilot.api.v1;

import br.com.fplbr.pilot.infrastructure.web.dto.PlanoDeVooDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class FplResourceTest {

    private PlanoDeVooDTO base() {
        PlanoDeVooDTO.InformacaoSuplementar info = PlanoDeVooDTO.InformacaoSuplementar.builder()
                .autonomia("0100")
                .corEMarcaAeronave("BRANCA/VERDE")
                .pilotoEmComando("PILOTO")
                .anacPrimeiroPiloto("123")
                .telefone("11985586633")
                .build();
        PlanoDeVooDTO.OutrasInformacoes outras = PlanoDeVooDTO.OutrasInformacoes.builder()
                .opr("OPERADOR")
                .from("SBMT")
                .dof("010101")
                .build();
        return PlanoDeVooDTO.builder()
                .identificacaoDaAeronave("PTOSP")
                .indicativoDeChamada(false)
                .regraDeVooEnum("V")
                .tipoDeVooEnum("G")
                .numeroDeAeronaves(1)
                .tipoDeAeronave("BL8")
                .categoriaEsteiraTurbulenciaEnum("L")
                .equipamentoCapacidadeDaAeronave(List.of("S"))
                .vigilancia(List.of("A"))
                .aerodromoDePartida("SBMT")
                .horaPartida(OffsetDateTime.now().plusMinutes(20))
                .aerodromoDeDestino("SBMT")
                .tempoDeVooPrevisto("0030")
                .aerodromoDeAlternativa("SBJD")
                .velocidadeDeCruzeiro("N0100")
                .nivelDeVoo("VFR")
                .rota("DCT")
                .outrasInformacoes(outras)
                .informacaoSuplementar(info)
                .modo("PVS")
                .build();
    }

    @Test
    void deveCriarFplHappyPath() {
        PlanoDeVooDTO dto = base();
        dto.setHoraPartida(OffsetDateTime.now().plusMinutes(20));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
        .when()
                .post("/api/v1/fpl")
        .then()
                .statusCode(201);
    }

    @Test
    void deveRejeitarR1AntecedenciaInsuficientePVS() {
        PlanoDeVooDTO dto = base();
        dto.setHoraPartida(OffsetDateTime.now().plusMinutes(5));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
        .when()
                .post("/api/v1/fpl")
        .then()
                .statusCode(400)
                .body("message", is("Hora de partida deve ser ≥ agora UTC + 15 min"));
    }

    @Test
    void deveRejeitarR2EetMaiorQueAutonomia() {
        PlanoDeVooDTO dto = base();
        dto.getInformacaoSuplementar().setAutonomia("0020");
        dto.setTempoDeVooPrevisto("0030");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
        .when()
                .post("/api/v1/fpl")
        .then()
                .statusCode(400)
                .body("message", is("EET não pode exceder a autonomia informada"));
    }
}


