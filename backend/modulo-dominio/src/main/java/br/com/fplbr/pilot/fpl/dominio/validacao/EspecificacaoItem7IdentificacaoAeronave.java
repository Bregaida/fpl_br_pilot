package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem7IdentificacaoAeronave implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        var id = alvo.getIdentificacaoAeronave();
        if (id == null || id.valor() == null || id.valor().isBlank()) {
            erros.add(new ErroRegra("ITEM7.VAZIO", "Identificação da aeronave é obrigatória."));
        } else {
            String v = id.valor().trim().toUpperCase();
            if (!v.matches("^[A-Z0-9]{2,7}$")) {
                erros.add(new ErroRegra("ITEM7.FORMATO", "Identificação deve ter 2 a 7 caracteres alfanuméricos (sem hífen)."));
            }
        }
        return erros;
    }
}
