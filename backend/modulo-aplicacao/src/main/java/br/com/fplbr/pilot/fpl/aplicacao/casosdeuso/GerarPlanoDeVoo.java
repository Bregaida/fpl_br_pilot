package br.com.fplbr.pilot.fpl.aplicacao.casosdeuso;

import br.com.fplbr.pilot.fpl.aplicacao.dto.RequisicaoPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.RespostaPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.ErroValidacaoDTO;
import br.com.fplbr.pilot.fpl.aplicacao.servicos.MontadorFplIcao;
import br.com.fplbr.pilot.fpl.dominio.modelo.*;
import br.com.fplbr.pilot.fpl.dominio.validacao.ValidadorPlanoDeVoo;

import java.util.List;
import java.util.ArrayList;

public class GerarPlanoDeVoo {
    /**
     * Conveniência: método único com parâmetros diretos para validar e montar o FPL.
     */
    public RespostaPlanoDeVoo executar(
            String identificacaoAeronave,
            String regrasDeVoo,
            String tipoDeVoo,
            Integer quantidadeAeronaves,
            String tipoAeronaveIcao,
            String esteira,
            String equipamentos,
            String aerodromoPartida,
            String horaPartidaZulu,
            String velocidadeCruzeiro,
            String nivelCruzeiro,
            String rota,
            String aerodromoDestino,
            String eetTotal,
            String alternativo1,
            String alternativo2,
            String outrosDados,
            String autonomia,
            Integer pessoasABordo,
            String piloto
    ) {
        RequisicaoPlanoDeVoo r = new RequisicaoPlanoDeVoo();
        r.identificacaoAeronave = identificacaoAeronave;
        r.regrasDeVoo = regrasDeVoo;
        r.tipoDeVoo = tipoDeVoo;
        r.quantidadeAeronaves = quantidadeAeronaves;
        r.tipoAeronaveIcao = tipoAeronaveIcao;
        r.esteira = esteira;
        r.equipamentos = equipamentos;
        r.aerodromoPartida = aerodromoPartida;
        r.horaPartidaZulu = horaPartidaZulu;
        r.velocidadeCruzeiro = velocidadeCruzeiro;
        r.nivelCruzeiro = nivelCruzeiro;
        r.rota = rota;
        r.aerodromoDestino = aerodromoDestino;
        r.eetTotal = eetTotal;
        r.alternativo1 = alternativo1;
        r.alternativo2 = alternativo2;
        r.outrosDados = outrosDados;
        r.autonomia = autonomia;
        r.pessoasABordo = pessoasABordo;
        r.piloto = piloto;
        return executar(r);
    }
    public RespostaPlanoDeVoo executar(RequisicaoPlanoDeVoo req) {
        // 1) Mapear DTO -> Domínio (com normalização simples)
        PlanoDeVoo plano = mapear(req);

        // 2) Validar
        var validador = new ValidadorPlanoDeVoo();
        var erros = validador.validar(plano);
        if (!erros.isEmpty()) {
            List<String> lista = erros.stream().map(e -> e.codigo() + ": " + e.mensagem()).toList();
            List<ErroValidacaoDTO> detalhados = erros.stream()
                    .map(e -> new ErroValidacaoDTO(e.codigo(), e.mensagem()))
                    .toList();
            return new RespostaPlanoDeVoo(null, List.of(), lista, detalhados);
        }

        // 3) Montar FPL ICAO
        var montador = new MontadorFplIcao();
        String mensagem = montador.montar(plano);
        return new RespostaPlanoDeVoo(mensagem, List.of(), List.of(), List.of());
    }

    private PlanoDeVoo mapear(RequisicaoPlanoDeVoo r) {
        String id = (r.identificacaoAeronave == null || r.identificacaoAeronave.isBlank())
                ? "TESTE" : r.identificacaoAeronave.trim().toUpperCase();

        RegrasDeVoo regras = parseRegras(r.regrasDeVoo);
        TipoDeVoo tipo = parseTipo(r.tipoDeVoo);
        EsteiraDeTurbulencia esteira = parseEsteira(r.esteira);

        Integer qtd = (r.quantidadeAeronaves == null || r.quantidadeAeronaves < 1) ? 1 : r.quantidadeAeronaves;
        String tipoIcao = r.tipoAeronaveIcao != null ? r.tipoAeronaveIcao.trim().toUpperCase() : "ZZZZ";

        String eqp = (r.equipamentos == null || r.equipamentos.isBlank()) ? "S" : r.equipamentos.trim().toUpperCase();

        String dep = r.aerodromoPartida != null ? r.aerodromoPartida.trim().toUpperCase() : "ZZZZ";
        String off = r.horaPartidaZulu != null ? r.horaPartidaZulu.trim() : "0000";

        String spd = r.velocidadeCruzeiro != null ? r.velocidadeCruzeiro.trim().toUpperCase() : "N0120";
        String lvl = r.nivelCruzeiro != null ? r.nivelCruzeiro.trim().toUpperCase() : "F090";
        String rota = r.rota != null ? r.rota.trim().toUpperCase() : "DCT";

        String dest = r.aerodromoDestino != null ? r.aerodromoDestino.trim().toUpperCase() : "ZZZZ";
        String eet = r.eetTotal != null ? r.eetTotal.trim() : "0001";
        String alt1 = r.alternativo1 != null ? r.alternativo1.trim().toUpperCase() : null;
        String alt2 = r.alternativo2 != null ? r.alternativo2.trim().toUpperCase() : null;

        String outros = r.outrosDados != null ? r.outrosDados.trim() : null;

        String e = r.autonomia != null ? r.autonomia.trim() : null;
        Integer pob = r.pessoasABordo;
        String piloto = r.piloto;

        return new PlanoDeVoo(
                new IdentificacaoAeronave(id),
                regras, tipo,
                qtd, tipoIcao, esteira,
                eqp,
                dep, off,
                spd, lvl, rota,
                dest, eet, alt1, alt2,
                outros,
                e, pob, piloto
        );
    }

    private RegrasDeVoo parseRegras(String v) {
        if (v == null) return null;
        String s = v.trim().toUpperCase();
        return switch (s) {
            case "I", "IFR" -> RegrasDeVoo.IFR;
            case "V", "VFR" -> RegrasDeVoo.VFR;
            case "Y", "IFR_PARA_VFR" -> RegrasDeVoo.IFR_PARA_VFR;
            case "Z", "VFR_PARA_IFR" -> RegrasDeVoo.VFR_PARA_IFR;
            default -> null;
        };
    }
    private TipoDeVoo parseTipo(String v) {
        if (v == null) return null;
        String s = v.trim().toUpperCase();
        return switch (s) {
            case "S", "REGULAR" -> TipoDeVoo.REGULAR;
            case "N", "NAO_REGULAR" -> TipoDeVoo.NAO_REGULAR;
            case "G", "GERAL" -> TipoDeVoo.GERAL;
            case "M", "MILITAR" -> TipoDeVoo.MILITAR;
            case "X", "OUTROS" -> TipoDeVoo.OUTROS;
            default -> null;
        };
    }
    private EsteiraDeTurbulencia parseEsteira(String v) {
        if (v == null) return EsteiraDeTurbulencia.MEDIA;
        String s = v.trim().toUpperCase();
        return switch (s) {
            case "J", "SUPER" -> EsteiraDeTurbulencia.SUPER;
            case "H", "PESADA" -> EsteiraDeTurbulencia.PESADA;
            case "M", "MEDIA" -> EsteiraDeTurbulencia.MEDIA;
            case "L", "LEVE" -> EsteiraDeTurbulencia.LEVE;
            default -> EsteiraDeTurbulencia.MEDIA;
        };
    }
}
