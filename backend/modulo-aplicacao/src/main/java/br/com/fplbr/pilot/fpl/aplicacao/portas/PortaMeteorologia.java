package br.com.fplbr.pilot.fpl.aplicacao.portas;

import br.com.fplbr.pilot.fpl.aplicacao.dto.BriefingMeteorologico;

public interface PortaMeteorologia {
    BriefingMeteorologico obterBriefing(String icao);
}




