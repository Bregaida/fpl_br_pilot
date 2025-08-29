package br.com.fplbr.pilot.fpl.aplicacao.servicos;

import br.com.fplbr.pilot.fpl.aplicacao.dto.FplDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FplMessageBuilder {

    /**
     * Monta a mensagem FPL a partir do DTO
     * @param fpl DTO com os dados do plano de voo
     * @return String formatada com a mensagem FPL
     * @throws IllegalArgumentException se houver dados inválidos
     */
    public String montar(FplDTO fpl) {
        if (fpl == null) {
            throw new IllegalArgumentException("DTO do plano de voo não pode ser nulo");
        }

        StringBuilder sb = new StringBuilder();
        
        // Cabeçalho
        sb.append("(FPL-").append(fpl.aircraftId).append("\n");
        
        // Item 8: Regras de voo/Tipo de voo
        sb.append("-").append(fpl.flightRules)
          .append("/").append(fpl.flightType).append("\n");
        
        // Item 9: Número de aeronaves/Tipo/Esteira
        sb.append("-").append(fpl.numberOfAircraft != null ? fpl.numberOfAircraft : "1")
          .append(fpl.aircraftType).append("/")
          .append(fpl.wakeTurbulence).append("\n");
        
        // Item 10: Equipamentos
        sb.append("-")
          .append(fpl.equipA != null ? fpl.equipA.toString().replaceAll("[\\[\\] ]", "") : "N")
          .append(fpl.equipB != null ? "/" + fpl.equipB.toString().replaceAll("[\\[\\] ]", "") : "")
          .append("\n");
        
        // Item 13: Aeródromo de partida/Hora de partida
        sb.append("-").append(fpl.departureAerodrome)
          .append(fpl.departureTimeUTC).append("\n");
        
        // Item 15: Velocidade de cruzeiro/Nível/Rota
        sb.append("-").append(fpl.cruisingSpeed)
          .append(fpl.level)
          .append(" ").append(fpl.route).append("\n");
        
        // Item 16: Aeródromo de destino/Tempo total de voo/Alternativos
        sb.append("-").append(fpl.destinationAerodrome)
          .append(fpl.totalEet);
        
        if (fpl.alternate1 != null && !fpl.alternate1.isEmpty()) {
            sb.append(" ").append(fpl.alternate1);
        }
        if (fpl.alternate2 != null && !fpl.alternate2.isEmpty()) {
            sb.append(" ").append(fpl.alternate2);
        }
        sb.append("\n");
        
        // Item 18: Outras informações
        if (fpl.other != null) {
            sb.append("-");
            if (fpl.other.pbn != null && !fpl.other.pbn.isEmpty()) {
                sb.append("PBN/").append(fpl.other.pbn).append(" ");
            }
            if (fpl.other.performance != null && !fpl.other.performance.isEmpty()) {
                sb.append("PER/").append(fpl.other.performance).append(" ");
            }
            sb.append("\n");
        }
        
        // Item 19: Informações de emergência
        if (fpl.supp != null) {
            sb.append("-");
            
            // Pessoas a bordo (P/)
            if (fpl.supp.personsOnBoard != null) {
                sb.append("P/").append(fpl.supp.personsOnBoard).append(" ");
            }
            
            // Equipamentos de emergência (R/)
            StringBuilder radio = new StringBuilder();
            if (fpl.supp.radioUHF) radio.append("U");
            if (fpl.supp.radioVHF) radio.append("V");
            if (fpl.supp.elt) radio.append("E");
            if (radio.length() > 0) {
                sb.append("R/").append(radio).append(" ");
            }
            
            // Equipamentos de sobrevivência (S/)
            if (fpl.supp.survivalNone) {
                sb.append("S/NONE ");
            } else {
                StringBuilder survival = new StringBuilder();
                if (fpl.supp.surPolar) survival.append("P");
                if (fpl.supp.surDesert) survival.append("D");
                if (fpl.supp.surMaritime) survival.append("M");
                if (fpl.supp.surJungle) survival.append("J");
                if (survival.length() > 0) {
                    sb.append("S/").append(survival).append(" ");
                }
            }
            
            // Coletes (J/ e L/)
            if (fpl.supp.jackets) {
                sb.append("J/");
                if (fpl.supp.jacketLight) sb.append("L");
                if (fpl.supp.jacketFluorescent) sb.append("F");
                if (fpl.supp.jacketUHF) sb.append("U");
                if (fpl.supp.jacketVHF) sb.append("V");
                sb.append(" ");
            }
            
            // Botes (D/)
            if (fpl.supp.dinghies != null && fpl.supp.dinghies.number != null && fpl.supp.dinghies.number > 0) {
                sb.append("D/").append(fpl.supp.dinghies.number);
                if (fpl.supp.dinghies.capacity != null && fpl.supp.dinghies.capacity > 0) {
                    sb.append("x").append(fpl.supp.dinghies.capacity);
                }
                if (fpl.supp.dinghies.covered) sb.append("C");
                if (fpl.supp.dinghies.colour != null && !fpl.supp.dinghies.colour.isEmpty()) {
                    sb.append(" ").append(fpl.supp.dinghies.colour);
                }
                sb.append(" ");
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
