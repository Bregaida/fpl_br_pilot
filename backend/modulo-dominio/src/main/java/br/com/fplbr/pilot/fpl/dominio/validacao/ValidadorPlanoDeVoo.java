package br.com.fplbr.pilot.fpl.dominio.validacao;

import br.com.fplbr.pilot.fpl.dominio.modelo.PlanoDeVoo;
import java.util.ArrayList;
import java.util.List;

public class ValidadorPlanoDeVoo {

    private final List<Especificacao<PlanoDeVoo>> regras = new ArrayList<>();

    public ValidadorPlanoDeVoo() {
        regras.add(new EspecificacaoItem7IdentificacaoAeronave());
        regras.add(new EspecificacaoItem8RegrasETipoDeVoo());
        regras.add(new EspecificacaoItem9QuantidadeTipoEsteira());
        regras.add(new EspecificacaoItem13PartidaEHora());
        regras.add(new EspecificacaoItem10Equipamentos());
        regras.add(new EspecificacaoItem15RotaENiveis());
        regras.add(new EspecificacaoItem16DestinoEAlternativos());
        regras.add(new EspecificacaoItem18OutrosDados());
        regras.add(new EspecificacaoItem19AutonomiaEPessoas());
    }

    public List<ErroRegra> validar(PlanoDeVoo plano) {
        List<ErroRegra> erros = new ArrayList<>();
        for (var r : regras) {
            erros.addAll(r.validar(plano));
        }
        return erros;
    }
}
