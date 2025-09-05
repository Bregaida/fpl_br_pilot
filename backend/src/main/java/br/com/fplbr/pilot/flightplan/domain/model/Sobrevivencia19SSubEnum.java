package br.com.fplbr.pilot.flightplan.domain.model;

public enum Sobrevivencia19SSubEnum {
    P("P", "Polar"),
    D("D", "Deserto"),
    M("M", "Marítimo"),
    J("J", "Selva");

    private final String sigla;
    private final String descricao;
    
    Sobrevivencia19SSubEnum(String sigla, String descricao) {
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
