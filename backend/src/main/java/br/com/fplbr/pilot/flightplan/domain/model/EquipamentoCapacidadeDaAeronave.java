package br.com.fplbr.pilot.flightplan.domain.model;

import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the aircraft equipment and capabilities for ICAO Flight Plan Item 10a.
 * Each field corresponds to a specific equipment or capability indicator.
 */
@Embeddable
public class EquipamentoCapacidadeDaAeronave {
    // Builder pattern implementation
    public static EquipamentoCapacidadeDaAeronaveBuilder builder() {
        return new EquipamentoCapacidadeDaAeronaveBuilder();
    }

    public static class EquipamentoCapacidadeDaAeronaveBuilder {
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

        // Builder methods for each field
        public EquipamentoCapacidadeDaAeronaveBuilder n(boolean n) { this.n = n; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder s(boolean s) { this.s = s; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder a(boolean a) { this.a = a; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder b(boolean b) { this.b = b; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder c(boolean c) { this.c = c; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder d(boolean d) { this.d = d; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder e1(boolean e1) { this.e1 = e1; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder e2(boolean e2) { this.e2 = e2; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder e3(boolean e3) { this.e3 = e3; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder f(boolean f) { this.f = f; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder g(boolean g) { this.g = g; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder h(boolean h) { this.h = h; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder i(boolean i) { this.i = i; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j1(boolean j1) { this.j1 = j1; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j2(boolean j2) { this.j2 = j2; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j3(boolean j3) { this.j3 = j3; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j4(boolean j4) { this.j4 = j4; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j5(boolean j5) { this.j5 = j5; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j6(boolean j6) { this.j6 = j6; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder j7(boolean j7) { this.j7 = j7; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder k(boolean k) { this.k = k; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder l(boolean l) { this.l = l; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder m1(boolean m1) { this.m1 = m1; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder m2(boolean m2) { this.m2 = m2; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder m3(boolean m3) { this.m3 = m3; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder o(boolean o) { this.o = o; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p1(boolean p1) { this.p1 = p1; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p2(boolean p2) { this.p2 = p2; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p3(boolean p3) { this.p3 = p3; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p4(boolean p4) { this.p4 = p4; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p5(boolean p5) { this.p5 = p5; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p6(boolean p6) { this.p6 = p6; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p7(boolean p7) { this.p7 = p7; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p8(boolean p8) { this.p8 = p8; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder p9(boolean p9) { this.p9 = p9; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder r(boolean r) { this.r = r; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder t(boolean t) { this.t = t; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder u(boolean u) { this.u = u; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder v(boolean v) { this.v = v; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder w(boolean w) { this.w = w; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder x(boolean x) { this.x = x; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder y(boolean y) { this.y = y; return this; }
        public EquipamentoCapacidadeDaAeronaveBuilder z(boolean z) { this.z = z; return this; }

        public EquipamentoCapacidadeDaAeronave build() {
            return new EquipamentoCapacidadeDaAeronave(
                n, s, a, b, c, d, e1, e2, e3, f, g, h, i, j1, j2, j3, j4, j5, j6, j7,
                k, l, m1, m2, m3, o, p1, p2, p3, p4, p5, p6, p7, p8, p9, r, t, u, v, w, x, y, z
            );
        }
    }

    // Fields for each equipment/capability indicator in Item 10a
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

    // All-args constructor for builder
    public EquipamentoCapacidadeDaAeronave(
        boolean n, boolean s, boolean a, boolean b, boolean c, boolean d,
        boolean e1, boolean e2, boolean e3, boolean f, boolean g, boolean h, boolean i,
        boolean j1, boolean j2, boolean j3, boolean j4, boolean j5, boolean j6, boolean j7,
        boolean k, boolean l, boolean m1, boolean m2, boolean m3, boolean o,
        boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, boolean p8, boolean p9,
        boolean r, boolean t, boolean u, boolean v, boolean w, boolean x, boolean y, boolean z
    ) {
        this.n = n;
        this.s = s;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j1 = j1;
        this.j2 = j2;
        this.j3 = j3;
        this.j4 = j4;
        this.j5 = j5;
        this.j6 = j6;
        this.j7 = j7;
        this.k = k;
        this.l = l;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.o = o;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
        this.p7 = p7;
        this.p8 = p8;
        this.p9 = p9;
        this.r = r;
        this.t = t;
        this.u = u;
        this.v = v;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // No-args constructor for frameworks
    protected EquipamentoCapacidadeDaAeronave() {
        this.n = false;
        this.s = false;
        this.a = false;
        this.b = false;
        this.c = false;
        this.d = false;
        this.e1 = false;
        this.e2 = false;
        this.e3 = false;
        this.f = false;
        this.g = false;
        this.h = false;
        this.i = false;
        this.j1 = false;
        this.j2 = false;
        this.j3 = false;
        this.j4 = false;
        this.j5 = false;
        this.j6 = false;
        this.j7 = false;
        this.k = false;
        this.l = false;
        this.m1 = false;
        this.m2 = false;
        this.m3 = false;
        this.o = false;
        this.p1 = false;
        this.p2 = false;
        this.p3 = false;
        this.p4 = false;
        this.p5 = false;
        this.p6 = false;
        this.p7 = false;
        this.p8 = false;
        this.p9 = false;
        this.r = false;
        this.t = false;
        this.u = false;
        this.v = false;
        this.w = false;
        this.x = false;
        this.y = false;
        this.z = false;
    }

    /**
     * Fixed order for stable output as per ICAO Doc 4444
     */
    private static final List<EquipamentoCampo10AEnum> ORDEM = List.of(
        EquipamentoCampo10AEnum.N,
        EquipamentoCampo10AEnum.S,
        EquipamentoCampo10AEnum.A, EquipamentoCampo10AEnum.B, EquipamentoCampo10AEnum.C, EquipamentoCampo10AEnum.D,
        EquipamentoCampo10AEnum.E1, EquipamentoCampo10AEnum.E2, EquipamentoCampo10AEnum.E3,
        EquipamentoCampo10AEnum.F, EquipamentoCampo10AEnum.G, EquipamentoCampo10AEnum.H, EquipamentoCampo10AEnum.I,
        EquipamentoCampo10AEnum.J1, EquipamentoCampo10AEnum.J2, EquipamentoCampo10AEnum.J3, EquipamentoCampo10AEnum.J4,
        EquipamentoCampo10AEnum.J5, EquipamentoCampo10AEnum.J6, EquipamentoCampo10AEnum.J7,
        EquipamentoCampo10AEnum.K, EquipamentoCampo10AEnum.L,
        EquipamentoCampo10AEnum.M1, EquipamentoCampo10AEnum.M2, EquipamentoCampo10AEnum.M3,
        EquipamentoCampo10AEnum.O,
        EquipamentoCampo10AEnum.P1, EquipamentoCampo10AEnum.P2, EquipamentoCampo10AEnum.P3, EquipamentoCampo10AEnum.P4,
        EquipamentoCampo10AEnum.P5, EquipamentoCampo10AEnum.P6, EquipamentoCampo10AEnum.P7, EquipamentoCampo10AEnum.P8,
        EquipamentoCampo10AEnum.P9, EquipamentoCampo10AEnum.R, EquipamentoCampo10AEnum.T, EquipamentoCampo10AEnum.U,
        EquipamentoCampo10AEnum.V, EquipamentoCampo10AEnum.W, EquipamentoCampo10AEnum.X, EquipamentoCampo10AEnum.Y,
        EquipamentoCampo10AEnum.Z
    );

    // Getters and setters for all fields
    public boolean isN() { return n; }
    public void setN(boolean n) { this.n = n; }
    public boolean isS() { return s; }
    public void setS(boolean s) { this.s = s; }
    public boolean isA() { return a; }
    public void setA(boolean a) { this.a = a; }
    public boolean isB() { return b; }
    public void setB(boolean b) { this.b = b; }
    public boolean isC() { return c; }
    public void setC(boolean c) { this.c = c; }
    public boolean isD() { return d; }
    public void setD(boolean d) { this.d = d; }
    public boolean isE1() { return e1; }
    public void setE1(boolean e1) { this.e1 = e1; }
    public boolean isE2() { return e2; }
    public void setE2(boolean e2) { this.e2 = e2; }
    public boolean isE3() { return e3; }
    public void setE3(boolean e3) { this.e3 = e3; }
    public boolean isF() { return f; }
    public void setF(boolean f) { this.f = f; }
    public boolean isG() { return g; }
    public void setG(boolean g) { this.g = g; }
    public boolean isH() { return h; }
    public void setH(boolean h) { this.h = h; }
    public boolean isI() { return i; }
    public void setI(boolean i) { this.i = i; }
    public boolean isJ1() { return j1; }
    public void setJ1(boolean j1) { this.j1 = j1; }
    public boolean isJ2() { return j2; }
    public void setJ2(boolean j2) { this.j2 = j2; }
    public boolean isJ3() { return j3; }
    public void setJ3(boolean j3) { this.j3 = j3; }
    public boolean isJ4() { return j4; }
    public void setJ4(boolean j4) { this.j4 = j4; }
    public boolean isJ5() { return j5; }
    public void setJ5(boolean j5) { this.j5 = j5; }
    public boolean isJ6() { return j6; }
    public void setJ6(boolean j6) { this.j6 = j6; }
    public boolean isJ7() { return j7; }
    public void setJ7(boolean j7) { this.j7 = j7; }
    public boolean isK() { return k; }
    public void setK(boolean k) { this.k = k; }
    public boolean isL() { return l; }
    public void setL(boolean l) { this.l = l; }
    public boolean isM1() { return m1; }
    public void setM1(boolean m1) { this.m1 = m1; }
    public boolean isM2() { return m2; }
    public void setM2(boolean m2) { this.m2 = m2; }
    public boolean isM3() { return m3; }
    public void setM3(boolean m3) { this.m3 = m3; }
    public boolean isO() { return o; }
    public void setO(boolean o) { this.o = o; }
    public boolean isP1() { return p1; }
    public void setP1(boolean p1) { this.p1 = p1; }
    public boolean isP2() { return p2; }
    public void setP2(boolean p2) { this.p2 = p2; }
    public boolean isP3() { return p3; }
    public void setP3(boolean p3) { this.p3 = p3; }
    public boolean isP4() { return p4; }
    public void setP4(boolean p4) { this.p4 = p4; }
    public boolean isP5() { return p5; }
    public void setP5(boolean p5) { this.p5 = p5; }
    public boolean isP6() { return p6; }
    public void setP6(boolean p6) { this.p6 = p6; }
    public boolean isP7() { return p7; }
    public void setP7(boolean p7) { this.p7 = p7; }
    public boolean isP8() { return p8; }
    public void setP8(boolean p8) { this.p8 = p8; }
    public boolean isP9() { return p9; }
    public void setP9(boolean p9) { this.p9 = p9; }
    public boolean isR() { return r; }
    public void setR(boolean r) { this.r = r; }
    public boolean isT() { return t; }
    public void setT(boolean t) { this.t = t; }
    public boolean isU() { return u; }
    public void setU(boolean u) { this.u = u; }
    public boolean isV() { return v; }
    public void setV(boolean v) { this.v = v; }
    public boolean isW() { return w; }
    public void setW(boolean w) { this.w = w; }
    public boolean isX() { return x; }
    public void setX(boolean x) { this.x = x; }
    public boolean isY() { return y; }
    public void setY(boolean y) { this.y = y; }
    public boolean isZ() { return z; }
    public void setZ(boolean z) { this.z = z; }

    // toBuilder method for immutability pattern
    public EquipamentoCapacidadeDaAeronaveBuilder toBuilder() {
        return new EquipamentoCapacidadeDaAeronaveBuilder()
            .n(n).s(s).a(a).b(b).c(c).d(d)
            .e1(e1).e2(e2).e3(e3).f(f).g(g).h(h).i(i)
            .j1(j1).j2(j2).j3(j3).j4(j4).j5(j5).j6(j6).j7(j7)
            .k(k).l(l).m1(m1).m2(m2).m3(m3).o(o)
            .p1(p1).p2(p2).p3(p3).p4(p4).p5(p5).p6(p6).p7(p7).p8(p8).p9(p9)
            .r(r).t(t).u(u).v(v).w(w).x(x).y(y).z(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipamentoCapacidadeDaAeronave that = (EquipamentoCapacidadeDaAeronave) o;

        // Compare all boolean fields
        return this.n == that.n && this.s == that.s && this.a == that.a &&
               this.b == that.b && this.c == that.c && this.d == that.d &&
               this.e1 == that.e1 && this.e2 == that.e2 && this.e3 == that.e3 &&
               this.f == that.f && this.g == that.g && this.h == that.h &&
               this.i == that.i && this.j1 == that.j1 && this.j2 == that.j2 &&
               this.j3 == that.j3 && this.j4 == that.j4 && this.j5 == that.j5 &&
               this.j6 == that.j6 && this.j7 == that.j7 && this.k == that.k &&
               this.l == that.l && this.m1 == that.m1 && this.m2 == that.m2 &&
               this.m3 == that.m3 && this.o == that.o && this.p1 == that.p1 &&
               this.p2 == that.p2 && this.p3 == that.p3 && this.p4 == that.p4 &&
               this.p5 == that.p5 && this.p6 == that.p6 && this.p7 == that.p7 &&
               this.p8 == that.p8 && this.p9 == that.p9 && this.r == that.r &&
               this.t == that.t && this.u == that.u && this.v == that.v &&
               this.w == that.w && this.x == that.x && this.y == that.y &&
               this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, s, a, b, c, d, e1, e2, e3, f, g, h, i, j1, j2, j3, j4, j5, j6, j7,
                           k, l, m1, m2, m3, o, p1, p2, p3, p4, p5, p6, p7, p8, p9, r, t, u, v, w, x, y, z);
    }

    @Override
    public String toString() {
        return "EquipamentoCapacidadeDaAeronave{" +
               "n=" + n + ", s=" + s + ", a=" + a + ", b=" + b + ", c=" + c + ", d=" + d +
               ", e1=" + e1 + ", e2=" + e2 + ", e3=" + e3 + ", f=" + f + ", g=" + g + ", h=" + h + ", i=" + i +
               ", j1=" + j1 + ", j2=" + j2 + ", j3=" + j3 + ", j4=" + j4 + ", j5=" + j5 + ", j6=" + j6 + ", j7=" + j7 +
               ", k=" + k + ", l=" + l + ", m1=" + m1 + ", m2=" + m2 + ", m3=" + m3 + ", o=" + o +
               ", p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", p4=" + p4 + ", p5=" + p5 + ", p6=" + p6 +
               ", p7=" + p7 + ", p8=" + p8 + ", p9=" + p9 + ", r=" + r + ", t=" + t + ", u=" + u + ", v=" + v +
               ", w=" + w + ", x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    /**
     * Returns a list of selected enums with applied rules (e.g., 'N' overrides others)
     *
     * @return List of selected equipment enums in the correct order, or just N if N is selected
     */
    public List<EquipamentoCampo10AEnum> selecionados() {
        List<EquipamentoCampo10AEnum> list = new ArrayList<>();

        // If N is selected, only return N (as per ICAO rules)
        if (n) {
            list.add(EquipamentoCampo10AEnum.N);
            return list;
        }

        // Add all selected equipment in the predefined order
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

        // Return the list in the predefined order, filtering out any nulls
        return list.stream()
            .filter(Objects::nonNull)
            .filter(ORDEM::contains)
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
     * Lista das siglas, ex.: ["S","D","G","R","Y"]
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
     * Fabricação a partir de um conjunto de enums
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
