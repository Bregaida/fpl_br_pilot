package br.com.fplbr.pilot.model;

public enum VigilanciaCampo10BEnum {
    // Regra geral
    N("N",  "Sem equipamentos"),

    // SSR Modos A/C
    A("A",  "Transponder Modo A (4 dígitos - 4096)"),
    C("C",  "Transponder Modo A (4 dígitos - 4096) e Modo C"),

    // ADS-C
    D1("D1","ADS-C com capacidades FANS 1/A"),
    G1("G1","ADS-C com capacidades ATN"),

    // SSR Modo S
    E("E",  "Modo S: ID aeronave + altitude de pressão + ADS-B (ES)"),
    H("H",  "Modo S: ID aeronave + altitude de pressão + vigilância melhorada"),
    I("I",  "Modo S: ID aeronave, sem altitude de pressão"),
    L("L",  "Modo S: ID aeronave + altitude de pressão + ADS-B (ES) + vigilância melhorada"),
    P("P",  "Modo S: sem ID da aeronave"),
    S("S",  "Modo S: ID aeronave + altitude de pressão"),
    X("X",  "Modo S: sem altitude de pressão e sem ID da aeronave"),

    // ADS-B
    B1("B1","ADS-B out 1090 MHz (especializada)"),
    B2("B2","ADS-B out/in 1090 MHz (especializada)"),
    U1("U1","ADS-B out via UAT"),
    U2("U2","ADS-B out/in via UAT"),
    V1("V1","ADS-B out via VDL Modo 4"),
    V2("V2","ADS-B out/in via VDL Modo 4");

    private final String sigla;
    private final String descricao;
    
    VigilanciaCampo10BEnum(String sigla, String descricao) {
        this.sigla = sigla;
        this.descricao = descricao;
    }
    
    public String getSigla() {
        return sigla;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
