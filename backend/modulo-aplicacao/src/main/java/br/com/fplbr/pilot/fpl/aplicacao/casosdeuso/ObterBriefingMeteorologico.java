package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.BriefingMeteorologico;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaMeteorologia;

public class ObterBriefingMeteorologico {
    private final PortaMeteorologia porta;

    public ObterBriefingMeteorologico(PortaMeteorologia porta) { this.porta = porta; }

    public BriefingMeteorologico executar(String icao) {
        return porta.obterBriefing(icao);
    }
}



