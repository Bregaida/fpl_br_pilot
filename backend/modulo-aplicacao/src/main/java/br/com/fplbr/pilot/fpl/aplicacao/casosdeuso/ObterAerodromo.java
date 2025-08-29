package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;

public class ObterAerodromo {
    private final PortaAisweb porta;

    public ObterAerodromo(PortaAisweb porta) { this.porta = porta; }

    public Aerodromo executar(String icao) { return porta.obterAerodromo(icao); }
}
