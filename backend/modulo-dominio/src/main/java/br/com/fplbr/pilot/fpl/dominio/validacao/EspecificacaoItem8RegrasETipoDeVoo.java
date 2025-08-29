package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem8RegrasETipoDeVoo implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        if (alvo.getRegrasDeVoo() == null) {
            erros.add(new ErroRegra("ITEM8.REGRAS", "Regras de voo (IFR/VFR/Y/Z) obrigatórias."));
        }
        if (alvo.getTipoDeVoo() == null) {
            erros.add(new ErroRegra("ITEM8.TIPO", "Tipo de voo (REGULAR/NAO_REGULAR/GERAL/MILITAR/OUTROS) obrigatório."));
        }
        return erros;
    }
}
