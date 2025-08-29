package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorPlanoDeVooTest {

    private PlanoDeVoo planoValido() {
        return new PlanoDeVoo(
                new IdentificacaoAeronave("PTABC"),
                RegrasDeVoo.IFR,
                TipoDeVoo.GERAL,
                1,
                "C172",
                EsteiraDeTurbulencia.LEVE,
                "SDFGRY/S",
                "SBSP",
                "1300",
                "N0100",
                "F090",
                "DCT UZ31 PAB DCT",
                "SBRJ",
                "0105",
                "SBJR",
                "SBRP",
                "DOF/20250829",
                "0245",
                4,
                "Bregaida"
        );
    }

    @Test
    void validaPlanoSemErros() {
        var validador = new ValidadorPlanoDeVoo();
        var erros = validador.validar(planoValido());
        assertNotNull(erros);
        assertTrue(erros.isEmpty(), "Nao era esperado erro: " + erros);
    }

    @Test
    void acusaErrosNosItens13e15() {
        var invalido = new PlanoDeVoo(
                new IdentificacaoAeronave("PTDEF"),
                RegrasDeVoo.IFR,
                TipoDeVoo.GERAL,
                1,
                "C172",
                EsteiraDeTurbulencia.LEVE,
                "S",
                "XXX", // inválido (formato incorreto)
                "1300",
                null,    // velocidade ausente (item 15)
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
        var validador = new ValidadorPlanoDeVoo();
        List<ErroRegra> erros = validador.validar(invalido);
        assertNotNull(erros);
        assertFalse(erros.isEmpty());
        String joined = String.join(";", erros.stream().map(e -> e.codigo()+"-"+e.mensagem()).toList());
        assertTrue(joined.contains("ITEM13") || joined.contains("ITEM 13"));
        assertTrue(joined.contains("ITEM15") || joined.contains("ITEM 15"));
    }
}
