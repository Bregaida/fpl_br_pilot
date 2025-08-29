package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem16DestinoEAlternativos implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String dest = alvo.getAerodromoDestino();
        String eet = alvo.getEetTotal();
        String alt1 = alvo.getAlternativo1();
        String alt2 = alvo.getAlternativo2();

        if (dest == null || !dest.matches("^[A-Z]{4}$")) {
            erros.add(new ErroRegra("ITEM16.DESTINO", "Aeródromo de destino deve ser ICAO (4 letras)."));
        }
        if (eet == null || !eet.matches("^\\d{4}$")) {
            erros.add(new ErroRegra("ITEM16.EET", "Duração total (EET) deve ser HHMM."));
        } else {
            int h = Integer.parseInt(eet.substring(0,2));
            int m = Integer.parseInt(eet.substring(2,4));
            if (h < 0 || h > 23 || m < 0 || m > 59) {
                erros.add(new ErroRegra("ITEM16.EET", "EET inválido (00:00–23:59)."));
            }
        }
        if (alt1 != null && !alt1.isBlank() && !alt1.matches("^[A-Z]{4}$")) {
            erros.add(new ErroRegra("ITEM16.ALT1", "Alternativo 1 deve ser ICAO (4 letras)."));
        }
        if (alt2 != null && !alt2.isBlank() && !alt2.matches("^[A-Z]{4}$")) {
            erros.add(new ErroRegra("ITEM16.ALT2", "Alternativo 2 deve ser ICAO (4 letras)."));
        }
        return erros;
    }
}

