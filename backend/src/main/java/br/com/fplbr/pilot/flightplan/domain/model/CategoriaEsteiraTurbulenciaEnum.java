package br.com.fplbr.pilot.flightplan.domain.model;

public enum CategoriaEsteiraTurbulenciaEnum {
    LEVE(1, "L", "Leve"),
    MEDIA(2, "M", "MÃ©dia"),
    PESADO(3, "H", "Pesado"),
    SUPER(4, "J", "Super");

    private final int id;
    private final String codigo;
    private final String descricao;
    
    CategoriaEsteiraTurbulenciaEnum(int id, String codigo, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
}
