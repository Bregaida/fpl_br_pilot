package br.com.fplbr.pilot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoCapacidadeDaAeronave {

    // Um boolean por checkbox do Item 10a
    private boolean n;
    private boolean s;
    private boolean a;
    private boolean b;
    private boolean c;
    private boolean d;
    private boolean e1;
    private boolean e2;
    private boolean e3;
    private boolean f;
    private boolean g;
    private boolean h;
    private boolean i;
    private boolean j1;
    private boolean j2;
    private boolean j3;
    private boolean j4;
    private boolean j5;
    private boolean j6;
    private boolean j7;
    private boolean k;
    private boolean l;
    private boolean m1;
    private boolean m2;
    private boolean m3;
    private boolean o;
    private boolean p1;
    private boolean p2;
    private boolean p3;
    private boolean p4;
    private boolean p5;
    private boolean p6;
    private boolean p7;
    private boolean p8;
    private boolean p9;
    private boolean r;
    private boolean t;
    private boolean u;
    private boolean v;
    private boolean w;
    private boolean x;
    private boolean y;
    private boolean z;

    // Ordem fixa para saída estável (recomendada no seu sistema)
    private static final List<EquipamentoCampo10AEnum> ORDEM = List.of(
        EquipamentoCampo10AEnum.N,
        EquipamentoCampo10AEnum.S,
        EquipamentoCampo10AEnum.A, EquipamentoCampo10AEnum.B, EquipamentoCampo10AEnum.C, EquipamentoCampo10AEnum.D,
        EquipamentoCampo10AEnum.E1, EquipamentoCampo10AEnum.E2, EquipamentoCampo10AEnum.E3,
        EquipamentoCampo10AEnum.F, EquipamentoCampo10AEnum.G, EquipamentoCampo10AEnum.H, EquipamentoCampo10AEnum.I,
        EquipamentoCampo10AEnum.J1, EquipamentoCampo10AEnum.J2, EquipamentoCampo10AEnum.J3, EquipamentoCampo10AEnum.J4, EquipamentoCampo10AEnum.J5, EquipamentoCampo10AEnum.J6, EquipamentoCampo10AEnum.J7,
        EquipamentoCampo10AEnum.K, EquipamentoCampo10AEnum.L,
        EquipamentoCampo10AEnum.M1, EquipamentoCampo10AEnum.M2, EquipamentoCampo10AEnum.M3,
        EquipamentoCampo10AEnum.O,
        EquipamentoCampo10AEnum.P1, EquipamentoCampo10AEnum.P2, EquipamentoCampo10AEnum.P3, EquipamentoCampo10AEnum.P4, EquipamentoCampo10AEnum.P5, EquipamentoCampo10AEnum.P6, EquipamentoCampo10AEnum.P7, EquipamentoCampo10AEnum.P8, EquipamentoCampo10AEnum.P9,
        EquipamentoCampo10AEnum.R, EquipamentoCampo10AEnum.T, EquipamentoCampo10AEnum.U, EquipamentoCampo10AEnum.V, EquipamentoCampo10AEnum.W, EquipamentoCampo10AEnum.X, EquipamentoCampo10AEnum.Y, EquipamentoCampo10AEnum.Z
    );

    /**
     * Lista de enum selecionados, já com regras aplicadas (ex.: N anula os demais)
     */
    public List<EquipamentoCampo10AEnum> selecionados() {
        List<EquipamentoCampo10AEnum> list = new ArrayList<>();
        if (n) list.add(EquipamentoCampo10AEnum.N);
        if (s) list.add(EquipamentoCampo10AEnum.S);
        if (a) list.add(EquipamentoCampo10AEnum.A);
        if (b) list.add(EquipamentoCampo10AEnum.B);
        if (c) list.add(EquipamentoCampo10AEnum.C);
        if (d) list.add(EquipamentoCampo10AEnum.D);
        if (e1) list.add(EquipamentoCampo10AEnum.E1);
        if (e2) list.add(EquipamentoCampo10AEnum.E2);
        if (e3) list.add(EquipamentoCampo10AEnum.E3);
        if (f) list.add(EquipamentoCampo10AEnum.F);
        if (g) list.add(EquipamentoCampo10AEnum.G);
        if (h) list.add(EquipamentoCampo10AEnum.H);
        if (i) list.add(EquipamentoCampo10AEnum.I);
        if (j1) list.add(EquipamentoCampo10AEnum.J1);
        if (j2) list.add(EquipamentoCampo10AEnum.J2);
        if (j3) list.add(EquipamentoCampo10AEnum.J3);
        if (j4) list.add(EquipamentoCampo10AEnum.J4);
        if (j5) list.add(EquipamentoCampo10AEnum.J5);
        if (j6) list.add(EquipamentoCampo10AEnum.J6);
        if (j7) list.add(EquipamentoCampo10AEnum.J7);
        if (k) list.add(EquipamentoCampo10AEnum.K);
        if (l) list.add(EquipamentoCampo10AEnum.L);
        if (m1) list.add(EquipamentoCampo10AEnum.M1);
        if (m2) list.add(EquipamentoCampo10AEnum.M2);
        if (m3) list.add(EquipamentoCampo10AEnum.M3);
        if (o) list.add(EquipamentoCampo10AEnum.O);
        if (p1) list.add(EquipamentoCampo10AEnum.P1);
        if (p2) list.add(EquipamentoCampo10AEnum.P2);
        if (p3) list.add(EquipamentoCampo10AEnum.P3);
        if (p4) list.add(EquipamentoCampo10AEnum.P4);
        if (p5) list.add(EquipamentoCampo10AEnum.P5);
        if (p6) list.add(EquipamentoCampo10AEnum.P6);
        if (p7) list.add(EquipamentoCampo10AEnum.P7);
        if (p8) list.add(EquipamentoCampo10AEnum.P8);
        if (p9) list.add(EquipamentoCampo10AEnum.P9);
        if (r) list.add(EquipamentoCampo10AEnum.R);
        if (t) list.add(EquipamentoCampo10AEnum.T);
        if (u) list.add(EquipamentoCampo10AEnum.U);
        if (v) list.add(EquipamentoCampo10AEnum.V);
        if (w) list.add(EquipamentoCampo10AEnum.W);
        if (x) list.add(EquipamentoCampo10AEnum.X);
        if (y) list.add(EquipamentoCampo10AEnum.Y);
        if (z) list.add(EquipamentoCampo10AEnum.Z);

        // Regra do Item 10a: se "N" (nenhum) estiver presente com outros, manter só N
        if (list.contains(EquipamentoCampo10AEnum.N) && list.size() > 1) {
            return List.of(EquipamentoCampo10AEnum.N);
        }

        // Ordenação estável pela lista ORDEM
        return list.stream()
            .sorted(Comparator.comparingInt(ORDEM::indexOf))
            .collect(Collectors.toList());
    }

    /**
     * Ex.: "SDGRY" (multi-letras como E1/J1 etc. entram como estão, sem separador)
     */
    public String siglasConcatenadas() {
        return selecionados().stream()
            .map(EquipamentoCampo10AEnum::getSigla)
            .collect(Collectors.joining());
    }

    /**
     * Lista só das siglas, ex.: ["S","D","G","R","Y"]
     */
    public List<String> siglasLista() {
        return selecionados().stream().map(EquipamentoCampo10AEnum::getSigla).toList();
    }

    /**
     * "S - equipamento padrão; D - DME; G - GNSS" (customize o separador)
     */
    public String siglaMaisDescricao(String separador) {
        return selecionados().stream()
            .map(e -> e.getSigla() + " - " + e.getDescricao())
            .collect(Collectors.joining(separador));
    }

    /**
     * Lista com "sigla - descrição"
     */
    public List<String> siglaMaisDescricaoLista() {
        return selecionados().stream()
            .map(e -> e.getSigla() + " - " + e.getDescricao())
            .toList();
    }

    /**
     * Atalho para usar no builder do FPL: Item 10a pronto
     */
    public String toItem10a() {
        return siglasConcatenadas();
    }

    /**
     * Fabricação a partir de um conjunto de enums (se preferir não usar booleans)
     */
    public static EquipamentoCapacidadeDaAeronave fromSet(Set<EquipamentoCampo10AEnum> set) {
        EquipamentoCapacidadeDaAeronave c = new EquipamentoCapacidadeDaAeronave();
        if (set == null || set.isEmpty()) return c;
        for (EquipamentoCampo10AEnum e : set) {
            switch (e) {
                case N -> c.setN(true);
                case S -> c.setS(true);
                case A -> c.setA(true);
                case B -> c.setB(true);
                case C -> c.setC(true);
                case D -> c.setD(true);
                case E1 -> c.setE1(true);
                case E2 -> c.setE2(true);
                case E3 -> c.setE3(true);
                case F -> c.setF(true);
                case G -> c.setG(true);
                case H -> c.setH(true);
                case I -> c.setI(true);
                case J1 -> c.setJ1(true);
                case J2 -> c.setJ2(true);
                case J3 -> c.setJ3(true);
                case J4 -> c.setJ4(true);
                case J5 -> c.setJ5(true);
                case J6 -> c.setJ6(true);
                case J7 -> c.setJ7(true);
                case K -> c.setK(true);
                case L -> c.setL(true);
                case M1 -> c.setM1(true);
                case M2 -> c.setM2(true);
                case M3 -> c.setM3(true);
                case O -> c.setO(true);
                case P1 -> c.setP1(true);
                case P2 -> c.setP2(true);
                case P3 -> c.setP3(true);
                case P4 -> c.setP4(true);
                case P5 -> c.setP5(true);
                case P6 -> c.setP6(true);
                case P7 -> c.setP7(true);
                case P8 -> c.setP8(true);
                case P9 -> c.setP9(true);
                case R -> c.setR(true);
                case T -> c.setT(true);
                case U -> c.setU(true);
                case V -> c.setV(true);
                case W -> c.setW(true);
                case X -> c.setX(true);
                case Y -> c.setY(true);
                case Z -> c.setZ(true);
            }
        }
        return c;
    }
}
