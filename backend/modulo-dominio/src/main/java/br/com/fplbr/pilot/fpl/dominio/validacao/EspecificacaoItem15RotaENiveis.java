package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class EspecificacaoItem15RotaENiveis implements Especificacao<PlanoDeVoo> {
    @Override
    public List<ErroRegra> validar(PlanoDeVoo alvo) {
        var erros = new ArrayList<ErroRegra>();
        String vel = alvo.getVelocidadeCruzeiro();
        String niv = alvo.getNivelCruzeiro();
        String rota = alvo.getRota();

        if (vel == null || !vel.matches("^(N\\d{4}|K\\d{4}|M\\d{3})$")) {
            erros.add(new ErroRegra("ITEM15.VELOCIDADE", "Velocidade cruzeiro deve ser Nxxxx/Kxxxx ou Mxxx."));
        }
        if (niv == null || !niv.matches("^([FA]\\d{3}|M\\d{3}|S\\d{3})$")) {
            erros.add(new ErroRegra("ITEM15.NIVEL", "Nível deve ser Fxxx/Axxx ou Mxxx/Sxxx."));
        }
        if (rota == null || rota.isBlank()) {
            erros.add(new ErroRegra("ITEM15.ROTA", "Rota é obrigatória."));
        }
        return erros;
    }
}
