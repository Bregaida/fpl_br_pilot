package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Frequencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representação de uma frequência de rádio na camada de apresentação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequenciaDTO {
    
    public enum TipoFrequenciaDTO {
        TWR,    // Torre de Controle
        GND,    // Solo
        ATIS,   // ATIS
        APCH,   // Aproximação
        DEP,    // Partida
        RADIO,  // Rádio
        OPR,    // Operadora
        RMP,    // Rampa
        TMA,    // Área Terminal
        INFO    // Informações
    }

    private TipoFrequenciaDTO tipo;
    private String descricao;
    private String valor;
    private String horarioFuncionamento;
    private String observacoes;

    /**
     * Converte uma entidade Frequencia para FrequenciaDTO.
     */
    public static FrequenciaDTO fromDomain(Frequencia frequencia) {
        if (frequencia == null) {
            return null;
        }

        return FrequenciaDTO.builder()
                .tipo(frequencia.getTipo() != null ? 
                        TipoFrequenciaDTO.valueOf(frequencia.getTipo().name()) : null)
                .descricao(frequencia.getDescricao())
                .valor(frequencia.getValor())
                .horarioFuncionamento(frequencia.getHorarioFuncionamento())
                .observacoes(frequencia.getObservacoes())
                .build();
    }

    /**
     * Converte este DTO para uma entidade de domínio Frequencia.
     */
    public Frequencia toDomain() {
        return Frequencia.builder()
                .tipo(this.tipo != null ? 
                        Frequencia.TipoFrequencia.valueOf(this.tipo.name()) : null)
                .descricao(this.descricao)
                .valor(this.valor)
                .horarioFuncionamento(this.horarioFuncionamento)
                .observacoes(this.observacoes)
                .build();
    }
}
