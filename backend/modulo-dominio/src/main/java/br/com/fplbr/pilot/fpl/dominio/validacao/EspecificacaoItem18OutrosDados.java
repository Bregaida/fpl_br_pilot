package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem18OutrosDados implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String outros = alvo.getOutrosDados();
        if (outros != null && !outros.isBlank()) {
            // Aceita conjunto simples de pares tipo ICAO (ex.: DOF/yyyymmdd, RMK/...)
            if (!outros.matches("^[A-Z]{3,4}/[A-Za-z0-9 .-]+( [A-Z]{3,4}/[A-Za-z0-9 .-]+)*$")) {
                erros.add(new ErroRegra("ITEM18.OUTROS", "Formato de OUTROS inválido (ex.: DOF/20250829 RMK/TEXTO)."));
            }
        }
        return erros;
    }
}
