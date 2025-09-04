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
public class Vigilancia {
    // Um boolean por checkbox do Item 10B
    private boolean n;

    // SSR Modos A/C
    private boolean a;
    private boolean c;

    // ADS-C
    private boolean d1;
    private boolean g1;

    // SSR Modo S
    private boolean e;
    private boolean h;
    private boolean i;
    private boolean l;
    private boolean p;
    private boolean s;
    private boolean x;

    // ADS-B
    private boolean b1;
    private boolean b2;
    private boolean u1;
    private boolean u2;
    private boolean v1;
    private boolean v2;

    // Ordem estável recomendada (N, SSR A/C, ADS-C, Modo S, ADS-B)
    private static final List<VigilanciaCampo10BEnum> ORDEM = List.of(
        VigilanciaCampo10BEnum.N,
        VigilanciaCampo10BEnum.A, VigilanciaCampo10BEnum.C,
        VigilanciaCampo10BEnum.D1, VigilanciaCampo10BEnum.G1,
        VigilanciaCampo10BEnum.E, VigilanciaCampo10BEnum.H, VigilanciaCampo10BEnum.I, VigilanciaCampo10BEnum.L, VigilanciaCampo10BEnum.P, VigilanciaCampo10BEnum.S, VigilanciaCampo10BEnum.X,
        VigilanciaCampo10BEnum.B1, VigilanciaCampo10BEnum.B2, VigilanciaCampo10BEnum.U1, VigilanciaCampo10BEnum.U2, VigilanciaCampo10BEnum.V1, VigilanciaCampo10BEnum.V2
    );

    /** Lista de enums selecionados, aplicando a regra do "N" */
    public List<VigilanciaCampo10BEnum> selecionados() {
        List<VigilanciaCampo10BEnum> list = new ArrayList<>();
        if (n)  list.add(VigilanciaCampo10BEnum.N);

        if (a)  list.add(VigilanciaCampo10BEnum.A);
        if (c)  list.add(VigilanciaCampo10BEnum.C);

        if (d1) list.add(VigilanciaCampo10BEnum.D1);
        if (g1) list.add(VigilanciaCampo10BEnum.G1);

        if (e)  list.add(VigilanciaCampo10BEnum.E);
        if (h)  list.add(VigilanciaCampo10BEnum.H);
        if (i)  list.add(VigilanciaCampo10BEnum.I);
        if (l)  list.add(VigilanciaCampo10BEnum.L);
        if (p)  list.add(VigilanciaCampo10BEnum.P);
        if (s)  list.add(VigilanciaCampo10BEnum.S);
        if (x)  list.add(VigilanciaCampo10BEnum.X);

        if (b1) list.add(VigilanciaCampo10BEnum.B1);
        if (b2) list.add(VigilanciaCampo10BEnum.B2);
        if (u1) list.add(VigilanciaCampo10BEnum.U1);
        if (u2) list.add(VigilanciaCampo10BEnum.U2);
        if (v1) list.add(VigilanciaCampo10BEnum.V1);
        if (v2) list.add(VigilanciaCampo10BEnum.V2);

        // Regra: se "N" estiver com outros, manter só N
        if (list.contains(VigilanciaCampo10BEnum.N) && list.size() > 1) {
            return List.of(VigilanciaCampo10BEnum.N);
        }

        return list.stream()
            .sorted(Comparator.comparingInt(ORDEM::indexOf))
            .collect(Collectors.toList());
    }

    /** Ex.: "CSG1S" (códigos de múltiplas letras entram como estão, sem separador) */
    public String siglasConcatenadas() {
        StringBuilder sb = new StringBuilder();
        for (VigilanciaCampo10BEnum e : selecionados()) {
            sb.append(e.getSigla());
        }
        return sb.toString();
    }

    /** Lista só das siglas, ex.: ["C","S","G1"] */
    public List<String> siglasLista() {
        return selecionados().stream()
            .map(e -> e.getSigla())
            .collect(Collectors.toList());
    }

    /** "S - ...; G1 - ..." (customize o separador) */
    public String siglaMaisDescricao(String separador) {
        return selecionados().stream()
            .map(e -> e.getSigla() + " - " + e.getDescricao())
            .collect(Collectors.joining(separador));
    }

    /** Lista com "sigla - descrição" */
    public List<String> siglaMaisDescricaoLista() {
        List<String> result = new ArrayList<>();
        for (VigilanciaCampo10BEnum e : selecionados()) {
            result.add(e.getSigla() + " - " + e.getDescricao());
        }
        return result;
    }

    /** Atalho para usar no builder do FPL: Item 10b pronto */
    public String toItem10b() {
        return siglasConcatenadas();
    }

    /** Cria a entidade a partir de um Set de enums (opcional) */
    public static Vigilancia fromSet(Set<VigilanciaCampo10BEnum> set) {
        Vigilancia v = new Vigilancia();
        if (set == null || set.isEmpty()) return v;
        
        // Manually set the fields instead of using setter methods
        for (VigilanciaCampo10BEnum e : set) {
            switch (e) {
                case N -> v.n = true;
                case A -> v.a = true;
                case C -> v.c = true;
                case D1 -> v.d1 = true;
                case G1 -> v.g1 = true;
                case E -> v.e = true;
                case H -> v.h = true;
                case I -> v.i = true;
                case L -> v.l = true;
                case P -> v.p = true;
                case S -> v.s = true;
                case X -> v.x = true;
                case B1 -> v.b1 = true;
                case B2 -> v.b2 = true;
                case U1 -> v.u1 = true;
                case U2 -> v.u2 = true;
                case V1 -> v.v1 = true;
                case V2 -> v.v2 = true;
            }
        }
        return v;
    }
}
