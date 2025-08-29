package br.com.fplbr.pilot.fpl.aplicacao.servicos;

import br.com.fplbr.pilot.fpl.dominio.modelo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MontadorFplIcaoTest {

    @Test
    void deveMontarMensagemFplCompleta() {
        PlanoDeVoo p = new PlanoDeVoo(
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
                "DOF/20250829 RMK/ENS",
                "0245",
                4,
                "Bregaida"
        );

        MontadorFplIcao m = new MontadorFplIcao();
        String fpl = m.montar(p);

        assertNotNull(fpl);
        assertTrue(fpl.startsWith("(FPL-PTABC"));
        assertTrue(fpl.contains("-IFR/G"));
        assertTrue(fpl.contains("-C172/L"));
        assertTrue(fpl.contains("-SBSP1300"));
        assertTrue(fpl.contains("-N0100F090 DCT UZ31 PAB DCT"));
        assertTrue(fpl.contains("-SBRJ0105 SBJR SBRP"));
        assertTrue(fpl.endsWith(")"));
    }
}


