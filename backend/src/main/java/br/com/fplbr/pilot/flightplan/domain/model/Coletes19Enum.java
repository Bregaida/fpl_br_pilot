package br.com.fplbr.pilot.flightplan.domain.model;

public enum Coletes19Enum {
    L("L", "Luz"),
    F("F", "Fluorescente"),
    U("U", "UHF"),
    V("V", "VHF");

    private final String sigla;
    private final String descricao;
    
    Coletes19Enum(String sigla, String descricao) {
        this.sigla = sigla;
        this.descricao = descricao;
    }
    
    public String getSigla() {
        return sigla;
    }
    
    public String getCodigo() {
        return sigla; // Alias for getSigla() to match the expected method name in EquipamentoEmergeciaSobrevivencia
    }
    
    public String getDescricao() {
        return descricao;
    }
}
