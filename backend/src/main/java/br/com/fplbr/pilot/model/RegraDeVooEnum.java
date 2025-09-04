package br.com.fplbr.pilot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegraDeVooEnum {
    IFR(1, "I", "IFR"),
    VFR(2, "V", "VFR"),
    IFR_VFR(3, "Y", "IFR/VFR"),
    VFR_IFR(4, "Y", "VFR/IFR");

    private final int id;
    private final String sigla;
    private final String descricao;
    
    /**
     * Verifica se a regra de voo é IFR.
     * @return true se for IFR, false caso contrário
     */
    public boolean isIFR() {
        return this == IFR || this == IFR_VFR || this == VFR_IFR;
    }
}
