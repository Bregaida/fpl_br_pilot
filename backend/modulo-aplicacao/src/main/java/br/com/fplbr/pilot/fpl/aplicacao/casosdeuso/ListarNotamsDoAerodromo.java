package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Notam;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;

import java.util.List;

public class ListarNotamsDoAerodromo {
    private final PortaAisweb porta;

    public ListarNotamsDoAerodromo(PortaAisweb porta) { this.porta = porta; }

    public List<Notam> executar(String icao) { return porta.listarNotams(icao); }
}
