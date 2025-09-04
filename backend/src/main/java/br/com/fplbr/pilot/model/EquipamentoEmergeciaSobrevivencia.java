package br.com.fplbr.pilot.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoEmergeciaSobrevivencia {
    // -------------------------
    // E/ - Equipamento Rádio de Emergência
    // -------------------------
    private boolean eUhf;   // U
    private boolean eVhf;   // V
    private boolean eElt;   // E

    // -------------------------
    // S/ - Equipamento de Sobrevivência
    // -------------------------
    private boolean s;               // S habilita os subitens abaixo
    private boolean sPolar;          // P
    private boolean sDeserto;        // D
    private boolean sMaritimo;       // M
    private boolean sSelva;          // J

    // -------------------------
    // J/ - Coletes
    // -------------------------
    private boolean j;               // J habilita os subitens abaixo
    private boolean jLuz;            // L
    private boolean jFluores;        // F
    private boolean jUhf;            // U
    private boolean jVhf;            // V

    // -------------------------
    // D/ - Botes
    // -------------------------
    private boolean d;               // D habilita campos abaixo
    private Long    dNumero;         // obrigatório se D=true
    private Long    dCapacidade;     // obrigatório se D=true
    private boolean dAbrigo;         // C
    private String  dCor;            // obrigatório se D=true e dAbrigo=true

    // ===== Helpers de seleção =====

    public List<EmergenciaRadio19Enum> selecionadosE() {
        List<EmergenciaRadio19Enum> list = new ArrayList<>();
        if (eUhf) list.add(EmergenciaRadio19Enum.U);
        if (eVhf) list.add(EmergenciaRadio19Enum.V);
        if (eElt) list.add(EmergenciaRadio19Enum.E);
        return list;
    }

    public List<Sobrevivencia19SSubEnum> selecionadosS() {
        if (!s) return List.of();
        List<Sobrevivencia19SSubEnum> list = new ArrayList<>();
        if (sPolar)   list.add(Sobrevivencia19SSubEnum.P);
        if (sDeserto) list.add(Sobrevivencia19SSubEnum.D);
        if (sMaritimo)list.add(Sobrevivencia19SSubEnum.M);
        if (sSelva)   list.add(Sobrevivencia19SSubEnum.J);
        return list;
    }

    public List<Coletes19JSubEnum> selecionadosJ() {
        if (!j) return List.of();
        List<Coletes19JSubEnum> list = new ArrayList<>();
        if (jLuz)     list.add(Coletes19JSubEnum.L);
        if (jFluores) list.add(Coletes19JSubEnum.F);
        if (jUhf)     list.add(Coletes19JSubEnum.U);
        if (jVhf)     list.add(Coletes19JSubEnum.V);
        return list;
    }

    // ===== Saídas em siglas =====

    /** E/UV(E) — concatena na ordem U, V, E */
    public String toItem19E() {
        List<String> siglas = new ArrayList<>();
        for (EmergenciaRadio19Enum e : selecionadosE()) {
            siglas.add(e.getSigla());
        }
        if (siglas.isEmpty()) return "";
        return "E/" + String.join("", siglas);
    }

    /** S/PDMJ — se S=false, retorna vazio; se S=true sem subitens, retorna "S" */
    public String toItem19S() {
        if (!s) return "";
        List<String> sub = new ArrayList<>();
        for (Sobrevivencia19SSubEnum s : selecionadosS()) {
            sub.add(s.getSigla());
        }
        return sub.isEmpty() ? "S" : "S/" + String.join("", sub);
    }

    /** J/LFUV — se J=false, retorna vazio; se J=true sem subitens, retorna "J" */
    public String toItem19J() {
        if (!j) return "";
        List<String> sub = new ArrayList<>();
        for (Coletes19JSubEnum j : selecionadosJ()) {
            sub.add(j.getSigla());
        }
        return sub.isEmpty() ? "J" : "J/" + String.join("", sub);
    }

    /**
     * D/numero capacidade [C cor]
     * Ex.: D/2 8 C AMARELO
     */
    public String toItem19D() {
        if (!d) return "";
        List<String> erros = validarErros();
        if (!erros.isEmpty()) {
            throw new IllegalStateException(String.join("; ", erros));
        }
        StringBuilder sb = new StringBuilder("D/");
        sb.append(dNumero).append(" ").append(dCapacidade);
        if (dAbrigo) {
            sb.append(" C");
            if (dCor != null && !dCor.isBlank()) {
                sb.append(" ").append(dCor.trim());
            }
        }
        return sb.toString();
    }

    /** Junta os tokens não vazios em uma linha final para o Item 19 */
    public String toItem19() {
        return Arrays.asList(toItem19E(), toItem19S(), toItem19J(), toItem19D())
            .stream()
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(" "));
    }

    // ===== Saídas "sigla - descrição" (úteis para UI/relatórios) =====

    public List<String> eSiglaMaisDescricao() {
        List<String> result = new ArrayList<>();
        for (EmergenciaRadio19Enum e : selecionadosE()) {
            result.add(e.getSigla() + " - " + e.getDescricao());
        }
        return result;
    }

    public List<String> sSiglaMaisDescricao() {
        if (!s) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        out.add("S - Equipamento de Sobrevivência");
        for (Sobrevivencia19SSubEnum s : selecionadosS()) {
            out.add(s.getSigla() + " - " + s.getDescricao());
        }
        return out;
    }

    public List<String> jSiglaMaisDescricao() {
        if (!j) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        out.add("J - Coletes Salva-Vidas");
        for (Coletes19JSubEnum j : selecionadosJ()) {
            out.add(j.getSigla() + " - " + j.getDescricao());
        }
        return out;
    }

    public List<String> dSiglaMaisDescricao() {
        if (!d) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        out.add("D - Botes (Número: " + dNumero + ", Capacidade: " + dCapacidade + ")");
        if (dAbrigo) out.add("C - Abrigo" + ((dCor != null && !dCor.isBlank()) ? (" (Cor: " + dCor.trim() + ")") : ""));
        return out;
    }

    // ===== Validação das regras de obrigatoriedade =====

    /** Retorna a lista de erros de validação; vazia se tudo OK. */
    public List<String> validarErros() {
        List<String> erros = new ArrayList<>();
        if (d) {
            if (dNumero == null || dNumero <= 0) {
                erros.add("Item 19 D: 'Número' é obrigatório e deve ser > 0.");
            }
            if (dCapacidade == null || dCapacidade <= 0) {
                erros.add("Item 19 D: 'Capacidade' é obrigatória e deve ser > 0.");
            }
            if (dAbrigo && (dCor == null || dCor.isBlank())) {
                erros.add("Item 19 D: 'Cor' é obrigatória quando 'C' (Abrigo) está selecionado.");
            }
        }
        return erros;
    }
}
