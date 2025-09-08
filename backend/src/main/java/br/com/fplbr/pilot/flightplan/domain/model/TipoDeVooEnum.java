package br.com.fplbr.pilot.flightplan.domain.model;
public enum TipoDeVooEnum {
    GERAL(1, "G", "Geral"),
    SERVICOS_REGULARES(2, "S", "Serviços regulares"),
    TRANSPORTE_AEREO_NAO_REGULAR(3, "N", "Transporte aéreo não regular"),
    MILITAR(4, "M", "Militar"),
    VOO_NAO_CLASSIFICADO(5, "X", "Voos não classificado nas categorias anteriores");

    private final int id;
    private final String sigla;
    private final String descricao;

    TipoDeVooEnum(int id, String sigla, String descricao) {
        this.id = id;
        this.sigla = sigla;
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public String getSigla() { return sigla; }
    public String getDescricao() { return descricao; }
}
