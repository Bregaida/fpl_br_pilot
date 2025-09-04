package br.com.fplbr.pilot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoriaEsteiraTurbulenciaEnum {
    LEVE(1, "L", "Leve"),
    MEDIA(2, "M", "Média"),
    PESADO(3, "H", "Pesado"),
    SUPER(4, "J", "Super");

    private final int id;
    private final String sigla;
    private final String descricao;
}
