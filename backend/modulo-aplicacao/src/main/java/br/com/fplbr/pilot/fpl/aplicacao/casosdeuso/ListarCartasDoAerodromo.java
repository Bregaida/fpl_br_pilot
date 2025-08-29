package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.CartaAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;

import java.util.List;

public class ListarCartasDoAerodromo {
    private final PortaAisweb porta;

    public ListarCartasDoAerodromo(PortaAisweb porta) { this.porta = porta; }

    public List<CartaAerodromo> executar(String icao) { return porta.listarCartas(icao); }
}
