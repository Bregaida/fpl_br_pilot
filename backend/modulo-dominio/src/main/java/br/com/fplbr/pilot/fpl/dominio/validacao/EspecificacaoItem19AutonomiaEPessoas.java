package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem19AutonomiaEPessoas implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String auto = alvo.getAutonomia();
        Integer pob = alvo.getPessoasABordo();

        if (auto != null && !auto.isBlank()) {
            if (!auto.matches("^\\d{4}$")) {
                erros.add(new ErroRegra("ITEM19.AUTONOMIA", "Autonomia (E/) deve ser HHMM."));
            } else {
                int h = Integer.parseInt(auto.substring(0,2));
                int m = Integer.parseInt(auto.substring(2,4));
                if (h < 0 || h > 23 || m < 0 || m > 59) {
                    erros.add(new ErroRegra("ITEM19.AUTONOMIA", "Autonomia inválida (00:00–23:59)."));
                }
            }
        }
        if (pob != null) {
            if (pob < 0 || pob > 999) {
                erros.add(new ErroRegra("ITEM19.PESSOAS", "Pessoas a bordo (P/) deve ser entre 0 e 999."));
            }
        }
        return erros;
    }
}

