package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem13PartidaEHora implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String ad = alvo.getAerodromoPartida();
        String hhmm = alvo.getHoraPartidaZulu();

        if (ad == null || !ad.matches("^[A-Z]{4}$")) {
            erros.add(new ErroRegra("ITEM13.AERODROMO", "Aeródromo de partida deve ser ICAO (4 letras)."));
        }

        if (hhmm == null || !hhmm.matches("^\\d{4}$")) {
            erros.add(new ErroRegra("ITEM13.HORA", "Hora de partida deve ser HHMM UTC."));
        } else {
            int h = Integer.parseInt(hhmm.substring(0,2));
            int m = Integer.parseInt(hhmm.substring(2,4));
            if (h < 0 || h > 23 || m < 0 || m > 59) {
                erros.add(new ErroRegra("ITEM13.HORA", "Hora de partida inválida (00:00–23:59)."));
            }
        }
        return erros;
    }
}
