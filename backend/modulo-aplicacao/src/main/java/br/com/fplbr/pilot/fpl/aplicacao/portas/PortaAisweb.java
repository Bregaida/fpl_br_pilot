package br.com.fplbr.pilot.fpl.aplicacao.portas;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Notam;
import br.com.fplbr.pilot.fpl.aplicacao.dto.CartaAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;

import java.util.List;

public interface PortaAisweb {
    List<Notam> listarNotams(String icao);
    List<CartaAerodromo> listarCartas(String icao);
    Aerodromo obterAerodromo(String icao);
}




