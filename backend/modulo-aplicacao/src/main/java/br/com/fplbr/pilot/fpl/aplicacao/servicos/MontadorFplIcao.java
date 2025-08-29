package br.com.fplbr.pilot.fpl.aplicacao.servicos;

import br.com.fplbr.pilot.fpl.dominio.modelo.*;

public class MontadorFplIcao {

    public String montar(PlanoDeVoo p) {
        StringBuilder sb = new StringBuilder();
        sb.append("(FPL-").append(p.getIdentificacaoAeronave().valor()).append("\n");

        // Item 8: Regras/Tipo → ex: -IFR/G
        sb.append("-").append(mapRegras(p.getRegrasDeVoo()))
          .append("/").append(mapTipo(p.getTipoDeVoo())).append("\n");

        // Item 9: número/tipo/esteira → ex: -C172/L  (ou -2C172/L)
        String numero = p.getQuantidadeAeronaves() != null && p.getQuantidadeAeronaves() > 1
                ? String.valueOf(p.getQuantidadeAeronaves()) : "";
        sb.append("-").append(numero).append(p.getTipoAeronaveIcao()).append("/")
          .append(mapEsteira(p.getEsteira())).append("\n");

        // Item 10: equipamentos
        sb.append("-").append(p.getEquipamentos() != null ? p.getEquipamentos() : "S").append("\n");

        // Item 13: Partida/Hora
        sb.append("-").append(p.getAerodromoPartida()).append(p.getHoraPartidaZulu()).append("\n");

        // Item 15: Velocidade/Nível + rota
        sb.append("-").append(p.getVelocidadeCruzeiro()).append(p.getNivelCruzeiro())
          .append(" ").append(p.getRota()).append("\n");

        // Item 16: Destino/EET + alternativos (se houver)
        sb.append("-").append(p.getAerodromoDestino()).append(p.getEetTotal());
        if (notBlank(p.getAlternativo1())) sb.append(" ").append(p.getAlternativo1());
        if (notBlank(p.getAlternativo2())) sb.append(" ").append(p.getAlternativo2());
        sb.append("\n");

        // Item 18: Outros dados (opcional)
        if (notBlank(p.getOutrosDados())) {
            sb.append("-").append(p.getOutrosDados()).append("\n");
        }

        // Item 19 (mínimo: E/ e P/)
        boolean tem19 = false;
        StringBuilder l19 = new StringBuilder();
        if (notBlank(p.getAutonomia())) { l19.append("E/").append(p.getAutonomia()); tem19 = true; }
        if (p.getPessoasABordo() != null) { 
            if (tem19) l19.append(" ");
            l19.append("P/").append(String.format("%03d", Math.max(0, p.getPessoasABordo())));
            tem19 = true;
        }
        if (tem19) {
            sb.append("-").append(l19).append("\n");
        }

        sb.append(")");
        return sb.toString();
    }

    private static boolean notBlank(String s){ return s != null && !s.isBlank(); }

    private static String mapRegras(RegrasDeVoo r) {
        return switch (r) {
            case IFR -> "IFR";
            case VFR -> "VFR";
            case IFR_PARA_VFR -> "Y"; // Composite (ICAO: Y = IFR first then VFR)
            case VFR_PARA_IFR -> "Z"; // Composite (ICAO: Z = VFR first then IFR)
        };
    }
    private static String mapTipo(TipoDeVoo t) {
        return switch (t) {
            case REGULAR -> "S";       // Schedule
            case NAO_REGULAR -> "N";   // Non-schedule
            case GERAL -> "G";         // General
            case MILITAR -> "M";       // Military
            case OUTROS -> "X";        // Other
        };
    }
    private static String mapEsteira(EsteiraDeTurbulencia e) {
        return switch (e) {
            case SUPER -> "J";   // Super Heavy
            case PESADA -> "H";  // Heavy
            case MEDIA -> "M";   // Medium
            case LEVE -> "L";    // Light
        };
    }
}
