package br.com.fplbr.pilot.flightplan.domain.model;

import br.com.fplbr.pilot.flightplan.domain.model.Coletes19JSubEnum;
import br.com.fplbr.pilot.flightplan.domain.model.EmergenciaRadio19Enum;
import br.com.fplbr.pilot.flightplan.domain.model.Sobrevivencia19SSubEnum;
import jakarta.persistence.Embeddable;

import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class EquipamentoEmergeciaSobrevivencia {
    // -------------------------
    // E/ - Equipamento RÃƒÂ¡dio de EmergÃƒÂªncia
    // -------------------------
    private boolean eUhf;   // U
    private boolean eVhf;   // V
    private boolean eElt;   // E

    // -------------------------
    // S/ - Equipamento de SobrevivÃƒÂªncia
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
    private Long    dNumero;         // obrigatÃƒÂ³rio se D=true
    private Long    dCapacidade;     // obrigatÃƒÂ³rio se D=true
    private boolean dAbrigo;         // C
    private String  dCor;            // obrigatÃƒÂ³rio se D=true e dAbrigo=true

    // ===== Constructors =====

    public EquipamentoEmergeciaSobrevivencia() {
    }

    private EquipamentoEmergeciaSobrevivencia(boolean eUhf, boolean eVhf, boolean eElt,
                                             boolean s, boolean sPolar, boolean sDeserto,
                                             boolean sMaritimo, boolean sSelva, boolean j,
                                             boolean jLuz, boolean jFluores, boolean jUhf,
                                             boolean jVhf, boolean d, Long dNumero,
                                             Long dCapacidade, boolean dAbrigo, String dCor) {
        this.eUhf = eUhf;
        this.eVhf = eVhf;
        this.eElt = eElt;
        this.s = s;
        this.sPolar = sPolar;
        this.sDeserto = sDeserto;
        this.sMaritimo = sMaritimo;
        this.sSelva = sSelva;
        this.j = j;
        this.jLuz = jLuz;
        this.jFluores = jFluores;
        this.jUhf = jUhf;
        this.jVhf = jVhf;
        this.d = d;
        this.dNumero = dNumero;
        this.dCapacidade = dCapacidade;
        this.dAbrigo = dAbrigo;
        this.dCor = dCor;
    }

    // ===== Builder Pattern =====

    public static EquipamentoEmergeciaSobrevivenciaBuilder builder() {
        return new EquipamentoEmergeciaSobrevivenciaBuilder();
    }

    public static class EquipamentoEmergeciaSobrevivenciaBuilder {
        private boolean eUhf;
        private boolean eVhf;
        private boolean eElt;
        private boolean s;
        private boolean sPolar;
        private boolean sDeserto;
        private boolean sMaritimo;
        private boolean sSelva;
        private boolean j;
        private boolean jLuz;
        private boolean jFluores;
        private boolean jUhf;
        private boolean jVhf;
        private boolean d;
        private Long dNumero;
        private Long dCapacidade;
        private boolean dAbrigo;
        private String dCor;

        public EquipamentoEmergeciaSobrevivenciaBuilder eUhf(boolean eUhf) { this.eUhf = eUhf; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder eVhf(boolean eVhf) { this.eVhf = eVhf; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder eElt(boolean eElt) { this.eElt = eElt; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder s(boolean s) { this.s = s; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder sPolar(boolean sPolar) { this.sPolar = sPolar; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder sDeserto(boolean sDeserto) { this.sDeserto = sDeserto; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder sMaritimo(boolean sMaritimo) { this.sMaritimo = sMaritimo; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder sSelva(boolean sSelva) { this.sSelva = sSelva; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder j(boolean j) { this.j = j; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder jLuz(boolean jLuz) { this.jLuz = jLuz; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder jFluores(boolean jFluores) { this.jFluores = jFluores; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder jUhf(boolean jUhf) { this.jUhf = jUhf; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder jVhf(boolean jVhf) { this.jVhf = jVhf; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder d(boolean d) { this.d = d; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder dNumero(Long dNumero) { this.dNumero = dNumero; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder dCapacidade(Long dCapacidade) { this.dCapacidade = dCapacidade; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder dAbrigo(boolean dAbrigo) { this.dAbrigo = dAbrigo; return this; }
        public EquipamentoEmergeciaSobrevivenciaBuilder dCor(String dCor) { this.dCor = dCor; return this; }

        public EquipamentoEmergeciaSobrevivencia build() {
            return new EquipamentoEmergeciaSobrevivencia(
                eUhf, eVhf, eElt, s, sPolar, sDeserto, sMaritimo, sSelva,
                j, jLuz, jFluores, jUhf, jVhf, d, dNumero, dCapacidade, dAbrigo, dCor
            );
        }
    }

    // ===== Getters and Setters =====

    public boolean isEUhf() { return eUhf; }
    public void setEUhf(boolean eUhf) { this.eUhf = eUhf; }

    public boolean isEVhf() { return eVhf; }
    public void setEVhf(boolean eVhf) { this.eVhf = eVhf; }

    public boolean isEElt() { return eElt; }
    public void setEElt(boolean eElt) { this.eElt = eElt; }

    public boolean isS() { return s; }
    public void setS(boolean s) { this.s = s; }

    public boolean isSPolar() { return sPolar; }
    public void setSPolar(boolean sPolar) { this.sPolar = sPolar; }

    public boolean isSDeserto() { return sDeserto; }
    public void setSDeserto(boolean sDeserto) { this.sDeserto = sDeserto; }

    public boolean isSMaritimo() { return sMaritimo; }
    public void setSMaritimo(boolean sMaritimo) { this.sMaritimo = sMaritimo; }

    public boolean isSSelva() { return sSelva; }
    public void setSSelva(boolean sSelva) { this.sSelva = sSelva; }

    public boolean isJ() { return j; }
    public void setJ(boolean j) { this.j = j; }

    public boolean isJLuz() { return jLuz; }
    public void setJLuz(boolean jLuz) { this.jLuz = jLuz; }

    public boolean isJFluores() { return jFluores; }
    public void setJFluores(boolean jFluores) { this.jFluores = jFluores; }

    public boolean isJUhf() { return jUhf; }
    public void setJUhf(boolean jUhf) { this.jUhf = jUhf; }

    public boolean isJVhf() { return jVhf; }
    public void setJVhf(boolean jVhf) { this.jVhf = jVhf; }

    public boolean isD() { return d; }
    public void setD(boolean d) { this.d = d; }

    public Long getDNumero() { return dNumero; }
    public void setDNumero(Long dNumero) { this.dNumero = dNumero; }

    public Long getDCapacidade() { return dCapacidade; }
    public void setDCapacidade(Long dCapacidade) { this.dCapacidade = dCapacidade; }

    public boolean isDAbrigo() { return dAbrigo; }
    public void setDAbrigo(boolean dAbrigo) { this.dAbrigo = dAbrigo; }

    public String getDCor() { return dCor; }
    public void setDCor(String dCor) { this.dCor = dCor; }

    // ===== equals, hashCode, and toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipamentoEmergeciaSobrevivencia that = (EquipamentoEmergeciaSobrevivencia) o;
        return eUhf == that.eUhf &&
               eVhf == that.eVhf &&
               eElt == that.eElt &&
               s == that.s &&
               sPolar == that.sPolar &&
               sDeserto == that.sDeserto &&
               sMaritimo == that.sMaritimo &&
               sSelva == that.sSelva &&
               j == that.j &&
               jLuz == that.jLuz &&
               jFluores == that.jFluores &&
               jUhf == that.jUhf &&
               jVhf == that.jVhf &&
               d == that.d &&
               dAbrigo == that.dAbrigo &&
               Objects.equals(dNumero, that.dNumero) &&
               Objects.equals(dCapacidade, that.dCapacidade) &&
               Objects.equals(dCor, that.dCor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eUhf, eVhf, eElt, s, sPolar, sDeserto, sMaritimo, sSelva,
                          j, jLuz, jFluores, jUhf, jVhf, d, dNumero, dCapacidade, dAbrigo, dCor);
    }

    @Override
    public String toString() {
        return "EquipamentoEmergeciaSobrevivencia{" +
               "eUhf=" + eUhf +
               ", eVhf=" + eVhf +
               ", eElt=" + eElt +
               ", s=" + s +
               ", sPolar=" + sPolar +
               ", sDeserto=" + sDeserto +
               ", sMaritimo=" + sMaritimo +
               ", sSelva=" + sSelva +
               ", j=" + j +
               ", jLuz=" + jLuz +
               ", jFluores=" + jFluores +
               ", jUhf=" + jUhf +
               ", jVhf=" + jVhf +
               ", d=" + d +
               ", dNumero=" + dNumero +
               ", dCapacidade=" + dCapacidade +
               ", dAbrigo=" + dAbrigo +
               ", dCor='" + dCor + '\'' +
               '}';
    }

    // ===== Helpers de seleÃƒÂ§ÃƒÂ£o =====

    public List<EmergenciaRadio19Enum> selecionadosE() {
        List<EmergenciaRadio19Enum> list = new ArrayList<>();
        if (eUhf) list.add(EmergenciaRadio19Enum.U);
        if (eVhf) list.add(EmergenciaRadio19Enum.V);
        if (eElt) list.add(EmergenciaRadio19Enum.E);
        return list;
    }

    // Method removed - duplicate of selecionadosS() with Sobrevivencia19SSubEnum

    public List<Coletes19JSubEnum> selecionadosJ() {
        if (!j) return List.of();
        List<Coletes19JSubEnum> list = new ArrayList<>();
        if (jLuz)     list.add(Coletes19JSubEnum.L);
        if (jFluores) list.add(Coletes19JSubEnum.F);
        if (jUhf)     list.add(Coletes19JSubEnum.U);
        if (jVhf)     list.add(Coletes19JSubEnum.V);
        return list;
    }

    public String getEquipamentoRadio() {
        if (selecionadosE().isEmpty()) return "";
        return "E/" + selecionadosE().stream()
            .map(EmergenciaRadio19Enum::getSigla)
            .collect(Collectors.joining(""));
    }

    public String getEquipamentoSobrevivencia() {
        if (!s || selecionadosS().isEmpty()) return "";
        return "S/" + selecionadosS().stream()
            .map(Sobrevivencia19SSubEnum::getSigla)
            .collect(Collectors.joining(""));
    }

    public String getEquipamentoColetes() {
        if (!j || selecionadosJ().isEmpty()) return "";
        return "J/" + selecionadosJ().stream()
            .map(Coletes19JSubEnum::getSigla)
            .collect(Collectors.joining(""));
    }

    public String getEquipamentoBotes() {
        if (!d || dNumero == null || dCapacidade == null) return "";

        StringBuilder sb = new StringBuilder("D/");
        sb.append(dNumero);
        sb.append(dCapacidade);

        if (dAbrigo) {
            sb.append("C");
            if (dCor != null && !dCor.trim().isEmpty()) {
                sb.append(dCor);
            }
        }

        return sb.toString();
    }

    public static EquipamentoEmergeciaSobrevivencia fromString(String e, String s, String j, String d) {
        EquipamentoEmergeciaSobrevivenciaBuilder builder = builder();

        // Processa E/ - Equipamento RÃƒÂ¡dio de EmergÃƒÂªncia
        if (e != null && e.startsWith("E/")) {
            String codes = e.substring(2);
            builder.eUhf(codes.contains("U"))
                  .eVhf(codes.contains("V"))
                  .eElt(codes.contains("E"));
        }

        // Processa S/ - Equipamento de SobrevivÃƒÂªncia
        if (s != null && s.startsWith("S/")) {
            String codes = s.substring(2);
            builder.s(true)
                  .sPolar(codes.contains("P"))
                  .sDeserto(codes.contains("D"))
                  .sMaritimo(codes.contains("M"))
                  .sSelva(codes.contains("J"));
        }

        // Processa J/ - Coletes
        if (j != null && j.startsWith("J/")) {
            String codes = j.substring(2);
            builder.j(true)
                  .jLuz(codes.contains("L"))
                  .jFluores(codes.contains("F"))
                  .jUhf(codes.contains("U"))
                  .jVhf(codes.contains("V"));
        }

        // Processa D/ - Botes
        if (d != null && d.startsWith("D/")) {
            String dValue = d.substring(2);

            // Extrai nÃƒÂºmero de botes (primeiros dÃƒÂ­gitos)
            int numEnd = 0;
            while (numEnd < dValue.length() && Character.isDigit(dValue.charAt(numEnd))) {
                numEnd++;
            }

            if (numEnd > 0) {
                try {
                    Long numero = Long.parseLong(dValue.substring(0, numEnd));
                    builder.dNumero(numero);

                    // Extrai capacidade (prÃƒÂ³ximos dÃƒÂ­gitos)
                    int capStart = numEnd;
                    int capEnd = capStart;
                    while (capEnd < dValue.length() && Character.isDigit(dValue.charAt(capEnd))) {
                        capEnd++;
                    }

                    if (capEnd > capStart) {
                        Long capacidade = Long.parseLong(dValue.substring(capStart, capEnd));
                        builder.dCapacidade(capacidade);

                        // Verifica se tem abrigo (C) e cor
                        if (dValue.length() > capEnd) {
                            String rest = dValue.substring(capEnd);
                            if (rest.startsWith("C")) {
                                builder.dAbrigo(true);
                                if (rest.length() > 1) {
                                    builder.dCor(rest.substring(1));
                                }
                            }
                        }

                        builder.d(true);
                    }
                } catch (NumberFormatException ex) {
                    // Ignora erros de parsing
                }
            }
        }

        return builder.build();
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

    // ===== SaÃƒÂ­das em siglas =====

    /** E/UV(E) Ã¢â‚¬â€ concatena na ordem U, V, E */
    public String toItem19E() {
        List<String> siglas = new ArrayList<>();
        for (EmergenciaRadio19Enum e : selecionadosE()) {
            siglas.add(e.getSigla());
        }
        if (siglas.isEmpty()) return "";
        return "E/" + String.join("", siglas);
    }

    /** S/PDMJ Ã¢â‚¬â€ se S=false, retorna vazio; se S=true sem subitens, retorna "S" */
    public String toItem19S() {
        if (!s) return "";
        List<String> sub = new ArrayList<>();
        for (Sobrevivencia19SSubEnum s : selecionadosS()) {
            sub.add(s.getSigla());
        }
        return sub.isEmpty() ? "S" : "S/" + String.join("", sub);
    }

    /** J/LFUV Ã¢â‚¬â€ se J=false, retorna vazio; se J=true sem subitens, retorna "J" */
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

    /** Junta os tokens nÃƒÂ£o vazios em uma linha final para o Item 19 */
    public String toItem19() {
        return Arrays.asList(toItem19E(), toItem19S(), toItem19J(), toItem19D())
            .stream()
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(" "));
    }

    // ===== SaÃƒÂ­das "sigla - descriÃƒÂ§ÃƒÂ£o" (ÃƒÂºteis para UI/relatÃƒÂ³rios) =====

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
        out.add("S - Equipamento de SobrevivÃƒÂªncia");
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
        out.add("D - Botes (NÃƒÂºmero: " + dNumero + ", Capacidade: " + dCapacidade + ")");
        if (dAbrigo) out.add("C - Abrigo" + ((dCor != null && !dCor.isBlank()) ? (" (Cor: " + dCor.trim() + ")") : ""));
        return out;
    }

    // ===== ValidaÃƒÂ§ÃƒÂ£o das regras de obrigatoriedade =====

    /** Retorna a lista de erros de validaÃƒÂ§ÃƒÂ£o; vazia se tudo OK. */
    public List<String> validarErros() {
        List<String> erros = new ArrayList<>();
        if (d) {
            if (dNumero == null || dNumero <= 0) {
                erros.add("Item 19 D: 'NÃƒÂºmero' ÃƒÂ© obrigatÃƒÂ³rio e deve ser > 0.");
            }
            if (dCapacidade == null || dCapacidade <= 0) {
                erros.add("Item 19 D: 'Capacidade' ÃƒÂ© obrigatÃƒÂ³ria e deve ser > 0.");
            }
            if (dAbrigo && (dCor == null || dCor.isBlank())) {
                erros.add("Item 19 D: 'Cor' ÃƒÂ© obrigatÃƒÂ³ria quando 'C' (Abrigo) estÃƒÂ¡ selecionado.");
            }
        }
        return erros;
    }
}
