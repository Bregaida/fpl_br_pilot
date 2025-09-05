package br.com.fplbr.pilot.flightplan.domain.model;

public enum Coletes19JSubEnum {
    L("L", "Luz"),
    F("F", "Fluorescente"),
    U("U", "UHF"),
    V("V", "VHF");

    private final String sigla;
    private final String descricao;

    Coletes19JSubEnum(String sigla, String descricao) {
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

