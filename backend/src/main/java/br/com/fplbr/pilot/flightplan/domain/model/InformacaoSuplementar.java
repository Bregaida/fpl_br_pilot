package br.com.fplbr.pilot.flightplan.domain.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class InformacaoSuplementar {
    private LocalDateTime autonimia; //Aqui devo colocar HH:mm
    private Long pessoasABordo;
    private EquipamentoEmergeciaSobrevivencia equipamentoEmergeciaSobrevivencia;
    private String corMarcaAeronave;
    private boolean n;
    private String observacao;
    private String pilotoEmComando;
    private Long codigoAnacPrimeiroPiloto;
    private Long codigoAnacSegundoPiloto;
    private Long telefone;

    /**
     * Verifica se todas as informaÃƒÂ§ÃƒÂµes suplementares estÃƒÂ£o em branco (nulas ou vazias).
     * @return true se todas as informaÃƒÂ§ÃƒÂµes estiverem em branco, false caso contrÃƒÂ¡rio
     */
    public boolean isBlank() {
        return autonimia == null &&
               pessoasABordo == null &&
               equipamentoEmergeciaSobrevivencia == null &&
               (corMarcaAeronave == null || corMarcaAeronave.trim().isEmpty()) &&
               !n &&
               (observacao == null || observacao.trim().isEmpty()) &&
               (pilotoEmComando == null || pilotoEmComando.trim().isEmpty()) &&
               codigoAnacPrimeiroPiloto == null &&
               codigoAnacSegundoPiloto == null &&
               telefone == null;
    }
}
