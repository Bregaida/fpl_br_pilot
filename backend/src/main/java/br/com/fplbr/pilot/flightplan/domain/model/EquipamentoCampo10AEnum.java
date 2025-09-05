package br.com.fplbr.pilot.flightplan.domain.model;

public enum EquipamentoCampo10AEnum {
    N("N", "sem equipamentos"),
    S("S", "equipamento padrÃ£o"),
    A("A", "sistema de pouso GBAS"),
    B("B", "LPV (APV com SBAS)"),
    C("C", "LORAN C"),
    D("D", "DME"),
    E1("E1", "FMC WPR ACARS"),
    E2("E2", "D-FIS ACARS"),
    E3("E3", "PDC ACARS"),
    F("F", "ADF"),
    G("G", "GNSS"),
    H("H", "HF RTF"),
    I("I", "navegaÃ§Ã£o inercial"),
    J1("J1", "CPDLC ATN VDL Modo 2"),
    J2("J2", "CPDLC FANS 1/A HFDL"),
    J3("J3", "CPDLC FANS 1/A VDL Modo A"),
    J4("J4", "CPDLC FANS 1/A VDL Modo 2"),
    J5("J5", "CPDLC FANS 1/A SATCOM (INMARSAT)"),
    J6("J6", "CPDLC FANS 1/A SATCOM (MTSAT)"),
    J7("J7", "CPDLC FANS 1/A SATCOM (Iridium)"),
    K("K", "MSL"),
    L("L", "ILS"),
    M1("M1", "ATC RTF SATCOM (INMARSAT)"),
    M2("M2", "ATC RTF (MTSAT)"),
    M3("M3", "ATC RTF (Iridium)"),
    O("O", "VOR"),
    P1("P1", "CPDLC RCP 400"),
    P2("P2", "CPDLC RCP 240"),
    P3("P3", "SATVOICE RCP 400"),
    P4("P4", "Reservado para RCP"),
    P5("P5", "Reservado para RCP"),
    P6("P6", "Reservado para RCP"),
    P7("P7", "Reservado para RCP"),
    P8("P8", "Reservado para RCP"),
    P9("P9", "Reservado para RCP"),
    R("R", "aprovado PBN"),
    T("T", "TACAN"),
    U("U", "UHF RTF"),
    V("V", "VHF RTF"),
    W("W", "aprovado RVSM"),
    X("X", "aprovado MNPS"),
    Y("Y", "VHF com espaÃ§amento 8,33 kHz"),
    Z("Z", "outro equipamento/capacidade (detalhar no Item 18)");

    private final String sigla;
    private final String descricao;
    
    EquipamentoCampo10AEnum(String sigla, String descricao) {
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
