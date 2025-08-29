package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.RespostaPlanoDeVoo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GerarPlanoDeVooTest {

    @Test
    void deveGerarFplValido_comMetodoUnico() {
        GerarPlanoDeVoo caso = new GerarPlanoDeVoo();
        RespostaPlanoDeVoo resp = caso.executar(
                "PTABC",      // identificacaoAeronave
                "IFR",        // regrasDeVoo
                "GERAL",      // tipoDeVoo
                1,             // quantidadeAeronaves
                "C172",       // tipoAeronaveIcao
                "L",          // esteira
                "S",          // equipamentos
                "SBSP",       // aerodromoPartida
                "1300",       // horaPartidaZulu
                "N0100",      // velocidadeCruzeiro
                "F090",       // nivelCruzeiro
                "DCT UZ31 PAB DCT", // rota
                "SBRJ",       // aerodromoDestino
                "0105",       // eetTotal
                "SBJR",       // alternativo1
                "SBRP",       // alternativo2
                "DOF/20250829", // outrosDados
                "0245",       // autonomia
                4,             // pessoasABordo
                "Bregaida"    // piloto
        );
        assertNotNull(resp);
        assertTrue(resp.erros == null || resp.erros.isEmpty(), "Nao deve haver erros: " + resp.erros);
        assertNotNull(resp.mensagemFpl);
        assertTrue(resp.mensagemFpl.startsWith("(FPL-PTABC"));
    }

    @Test
    void deveRetornarErros_quandoCamposObrigatoriosInvalidos() {
        GerarPlanoDeVoo caso = new GerarPlanoDeVoo();
        // velocidadeCruzeiro em branco (vira invalida) e aerodromoPartida invalido (3 letras)
        RespostaPlanoDeVoo resp = caso.executar(
                "PTDEF",
                "IFR",
                "GERAL",
                1,
                "C172",
                "L",
                "S",
                "XXX",    // invÃ¡lido (nÃ£o ICAO)
                "1300",
                "",        // velocidadeCruzeiro em branco (Item 15)
                "F090",
                "DCT",
                "SBRJ",
                "0105",
                null,
                null,
                null,
                null,
                2,
                null
        );
        assertNotNull(resp);
        assertNull(resp.mensagemFpl);
        assertNotNull(resp.erros);
        assertFalse(resp.erros.isEmpty());
        // Deve conter referÃªncia aos itens 13 e 15
        String erros = String.join(";", resp.erros);
        assertTrue(erros.contains("ITEM13") || erros.contains("ITEM 13"));
        assertTrue(erros.contains("ITEM15") || erros.contains("ITEM 15"));
    }
}

