package br.com.fplbr.pilot.model;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDeVooEnum {
    GERAL(1, "G", "Geral"),
    SERVICOS_REGULARES(2, "S", "Serviços regulares"),
    TRANSPORTE_AEREO_NAO_REGULAR(3, "N", "Transporte aéreo não regular"),
    MILITAR(4, "M", "Militar"),
    VOO_NAO_CLASSIFICADO(5, "X", "Voos não classificado nas categorias anteriores");

    private final int id;
    private final String sigla;
    private final String descricao;
}
