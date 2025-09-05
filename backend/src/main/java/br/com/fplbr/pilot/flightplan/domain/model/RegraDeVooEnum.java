package br.com.fplbr.pilot.flightplan.domain.model;

public enum RegraDeVooEnum {
    IFR(1, "I", "IFR"),
    VFR(2, "V", "VFR"),
    IFR_VFR(3, "Y", "IFR/VFR"),
    VFR_IFR(4, "Y", "VFR/IFR");

    private final int id;
    private final String sigla;
    private final String descricao;
    
    RegraDeVooEnum(int id, String sigla, String descricao) {
        this.id = id;
        this.sigla = sigla;
        this.descricao = descricao;
    }
    
    public int getId() { return id; }
    public String getSigla() { return sigla; }
    public String getDescricao() { return descricao; }
    
    /**
     * Verifica se a regra de voo Ã© IFR.
     * @return true se for IFR, false caso contrÃ¡rio
     */
    public boolean isIFR() {
        return this == IFR || this == IFR_VFR || this == VFR_IFR;
    }
}
