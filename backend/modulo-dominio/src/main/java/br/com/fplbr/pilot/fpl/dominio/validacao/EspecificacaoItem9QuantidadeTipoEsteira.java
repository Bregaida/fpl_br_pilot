package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem9QuantidadeTipoEsteira implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        Integer qtd = alvo.getQuantidadeAeronaves();
        String tipo = alvo.getTipoAeronaveIcao();
        var esteira = alvo.getEsteira();

        if (qtd == null || qtd < 1 || qtd > 99) {
            erros.add(new ErroRegra("ITEM9.QUANTIDADE", "Quantidade de aeronaves deve estar entre 1 e 99."));
        }
        if (tipo == null || !tipo.matches("^[A-Z0-9]{2,4}$")) {
            erros.add(new ErroRegra("ITEM9.TIPO", "Tipo de aeronave ICAO deve ter 2 a 4 caracteres alfanuméricos."));
        }
        if (esteira == null) {
            erros.add(new ErroRegra("ITEM9.ESTEIRA", "Esteira de turbulência é obrigatória."));
        }
        return erros;
    }
}

