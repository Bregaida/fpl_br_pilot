package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;

/**
 * Caso de uso stub: retorna dados fictícios do aeródromo.
 * Integração real (AIS/BD interna) será adicionada depois via portas/adaptadores.
 */
public class ObterDetalhesDoAerodromo {

    public Aerodromo executar(String icao) {
        if (icao == null || icao.isBlank()) {
            icao = "XXXX";
        }
        return new Aerodromo(
                icao.toUpperCase(),
                "AERÓDROMO " + icao.toUpperCase(),
                "Município",
                "UF",
                null,
                null
        );
    }
}
