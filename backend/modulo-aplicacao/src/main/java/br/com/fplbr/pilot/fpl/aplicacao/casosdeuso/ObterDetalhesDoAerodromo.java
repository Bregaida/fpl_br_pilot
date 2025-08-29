package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;

/**
 * Caso de uso stub: retorna dados fictÃ­cios do aerÃ³dromo.
 * IntegraÃ§Ã£o real (AIS/BD interna) serÃ¡ adicionada depois via portas/adaptadores.
 */
public class ObterDetalhesDoAerodromo {

    public Aerodromo executar(String icao) {
        if (icao == null || icao.isBlank()) {
            icao = "XXXX";
        }
        return new Aerodromo(
                icao.toUpperCase(),
                "AERÃ“DROMO " + icao.toUpperCase(),
                "MunicÃ­pio",
                "UF",
                null,
                null
        );
    }
}




