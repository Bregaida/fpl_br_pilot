package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem10Equipamentos implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String eq = alvo.getEquipamentos();
        if (eq == null || eq.isBlank()) {
            erros.add(new ErroRegra("ITEM10.EQUIP", "Equipamentos não pode ser vazio."));
        } else if (!eq.matches("^[A-Z0-9/]+$")) {
            erros.add(new ErroRegra("ITEM10.EQUIP", "Equipamentos contém caracteres inválidos."));
        }
        return erros;
    }
}

