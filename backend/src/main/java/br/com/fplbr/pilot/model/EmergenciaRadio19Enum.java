package br.com.fplbr.pilot.model;

public enum EmergenciaRadio19Enum {
    U("U", "UHF"),
    V("V", "VHF"),
    E("E", "ELT");

    private final String sigla;
    private final String descricao;
    
    EmergenciaRadio19Enum(String sigla, String descricao) {
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
