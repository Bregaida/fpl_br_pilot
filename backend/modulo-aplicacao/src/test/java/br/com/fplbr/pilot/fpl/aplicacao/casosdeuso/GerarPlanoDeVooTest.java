package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.*;
import br.com.fplbr.pilot.fpl.aplicacao.servicos.FplMessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GerarPlanoDeVooTest {

    private FplMessageBuilder fplMessageBuilder;
    private GerarPlanoDeVoo gerarPlanoDeVoo;

    @BeforeEach
    void setUp() {
        fplMessageBuilder = Mockito.mock(FplMessageBuilder.class);
        gerarPlanoDeVoo = new GerarPlanoDeVoo();
        // Use reflection to inject the mock since fplBuilder is private
        try {
            var field = GerarPlanoDeVoo.class.getDeclaredField("fplBuilder");
            field.setAccessible(true);
            field.set(gerarPlanoDeVoo, fplMessageBuilder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock FplMessageBuilder", e);
        }
    }

    @Test
    void deveGerarFplValido_comMetodoUnico() {
        // Arrange
        when(fplMessageBuilder.montar(any(FplDTO.class)))
            .thenReturn("(FPL-PTABC-IFR-C172/M-SBSP1230-N0100F090 DCT UZ31 PAB DCT-SBRJ0105)");
        
        // Act
        RespostaPlanoDeVoo resp = gerarPlanoDeVoo.executar(
                "PTABC",      // identificacaoAeronave
                "IFR",        // regrasDeVoo
                "GERAL",      // tipoDeVoo
                1,            // quantidadeAeronaves
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
                4,            // pessoasABordo
                "Bregaida"    // piloto
        );
        
        // Assert
        assertNotNull(resp, "A resposta não deve ser nula");
        assertTrue(resp.erros == null || resp.erros.isEmpty(), 
                 "Não deve haver erros: " + (resp.erros != null ? String.join(", ", resp.erros) : ""));
        assertNotNull(resp.mensagemFpl, "A mensagem FPL não deve ser nula");
        assertTrue(resp.mensagemFpl.startsWith("(FPL-PTABC"), 
                 "A mensagem FPL deve começar com (FPL-PTABC, mas foi: " + 
                 (resp.mensagemFpl.length() > 20 ? resp.mensagemFpl.substring(0, 20) + "..." : resp.mensagemFpl));
    }

    @Test
    void deveRetornarErros_quandoCamposObrigatoriosInvalidos() {
        // Arrange
        when(fplMessageBuilder.montar(any(FplDTO.class)))
            .thenThrow(new IllegalArgumentException("Erro de validação"));
        
        // Act
        RespostaPlanoDeVoo resp = gerarPlanoDeVoo.executar(
                "PTDEF",      // identificacaoAeronave
                "IFR",        // regrasDeVoo
                "GERAL",      // tipoDeVoo
                1,            // quantidadeAeronaves
                "C172",       // tipoAeronaveIcao
                "L",          // esteira
                "S",          // equipamentos
                "XXX",        // aerodromoPartida inválido (não ICAO)
                "1300",       // horaPartidaZulu
                "",           // velocidadeCruzeiro em branco (Item 15)
                "F090",       // nivelCruzeiro
                "DCT",        // rota
                "SBRJ",       // aerodromoDestino
                "0105",       // eetTotal
                null,         // alternativo1
                null,         // alternativo2
                null,         // outrosDados
                null,         // autonomia
                2,            // pessoasABordo
                null          // piloto
        );
        
        // Assert
        assertNotNull(resp, "A resposta não deve ser nula");
        assertNull(resp.mensagemFpl, "Não deve haver mensagem FPL quando há erros");
        assertNotNull(resp.erros, "A lista de erros não deve ser nula");
        assertFalse(resp.erros.isEmpty(), "Deve haver pelo menos um erro de validação");
        
        // Verifica se há erros de validação
        String erros = String.join(";", resp.erros);
        assertTrue(erros.contains("Erro de validação") || 
                  erros.contains("ERRO_GERACAO"),
                  "Deve conter mensagem de erro de validação");
    }
}
