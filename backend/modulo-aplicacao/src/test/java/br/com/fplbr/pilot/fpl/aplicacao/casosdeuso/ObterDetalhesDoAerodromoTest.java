package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObterDetalhesDoAerodromoTest {

    @Test
    void deveRetornarAerodromoComIcaoUppercase() {
        ObterDetalhesDoAerodromo caso = new ObterDetalhesDoAerodromo();
        Aerodromo a = caso.executar("sbsp");
        assertEquals("SBSP", a.icao());
        assertNotNull(a.nome());
    }

    @Test
    void deveUsarXXXXQuandoIcaoInvalido() {
        ObterDetalhesDoAerodromo caso = new ObterDetalhesDoAerodromo();
        Aerodromo a = caso.executar("   ");
        assertEquals("XXXX", a.icao());
    }
}


