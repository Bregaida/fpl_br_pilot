package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Frequencia;
import java.util.Objects;

/**
 * DTO para representaÃ§Ã£o de uma frequÃªncia de rÃ¡dio na camada de apresentaÃ§Ã£o.
 */
public class FrequenciaDTO {
    public enum TipoFrequenciaDTO {
        TWR,    // Torre de Controle
        GND,    // Solo
        ATIS,   // ATIS
        APCH,   // AproximaÃ§Ã£o
        DEP,    // Partida
        RADIO,  // RÃ¡dio
        OPR,    // Operadora
        RMP,    // Rampa
        TMA,    // Ãrea Terminal
        INFO    // InformaÃ§Ãµes
    }

    // Fields
    private TipoFrequenciaDTO tipo;
    private String descricao;
    private String valor;
    private String horarioFuncionamento;
    private String observacoes;

    // No-args constructor
    public FrequenciaDTO() {
    }

    // All-args constructor for builder
    private FrequenciaDTO(TipoFrequenciaDTO tipo, String descricao, String valor, 
                         String horarioFuncionamento, String observacoes) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.horarioFuncionamento = horarioFuncionamento;
        this.observacoes = observacoes;
    }

    // Builder pattern
    public static FrequenciaDTOBuilder builder() {
        return new FrequenciaDTOBuilder();
    }

    public static class FrequenciaDTOBuilder {
        private TipoFrequenciaDTO tipo;
        private String descricao;
        private String valor;
        private String horarioFuncionamento;
        private String observacoes;

        public FrequenciaDTOBuilder tipo(TipoFrequenciaDTO tipo) { this.tipo = tipo; return this; }
        public FrequenciaDTOBuilder descricao(String descricao) { this.descricao = descricao; return this; }
        public FrequenciaDTOBuilder valor(String valor) { this.valor = valor; return this; }
        public FrequenciaDTOBuilder horarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; return this; }
        public FrequenciaDTOBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }

        public FrequenciaDTO build() {
            return new FrequenciaDTO(tipo, descricao, valor, horarioFuncionamento, observacoes);
        }
    }
    
    // Getters and Setters
    public TipoFrequenciaDTO getTipo() { return tipo; }
    public void setTipo(TipoFrequenciaDTO tipo) { this.tipo = tipo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    
    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // equals, hashCode, and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrequenciaDTO that = (FrequenciaDTO) o;
        return tipo == that.tipo &&
               Objects.equals(descricao, that.descricao) &&
               Objects.equals(valor, that.valor) &&
               Objects.equals(horarioFuncionamento, that.horarioFuncionamento) &&
               Objects.equals(observacoes, that.observacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, descricao, valor, horarioFuncionamento, observacoes);
    }

    @Override
    public String toString() {
        return "FrequenciaDTO{" +
               "tipo=" + tipo +
               ", descricao='" + descricao + '\'' +
               ", valor='" + valor + '\'' +
               ", horarioFuncionamento='" + horarioFuncionamento + '\'' +
               ", observacoes='" + observacoes + '\'' +
               '}';
    }

    /**
     * Converte uma entidade Frequencia para FrequenciaDTO.
     */
    public static FrequenciaDTO fromDomain(Frequencia frequencia) {
        if (frequencia == null) {
            return null;
        }

        return new FrequenciaDTOBuilder()
                .tipo(frequencia.getTipo() != null ? 
                        TipoFrequenciaDTO.valueOf(frequencia.getTipo().name()) : null)
                .descricao(frequencia.getDescricao())
                .valor(frequencia.getValor())
                .horarioFuncionamento(frequencia.getHorarioFuncionamento())
                .observacoes(frequencia.getObservacoes())
                .build();
    }

    /**
     * Converte este DTO para uma entidade de domÃ­nio Frequencia.
     */
    public Frequencia toDomain() {
        // Assuming Frequencia has a similar builder pattern implemented
        Frequencia.FrequenciaBuilder builder = Frequencia.builder()
                .tipo(this.tipo != null ? 
                        Frequencia.TipoFrequencia.valueOf(this.tipo.name()) : null)
                .descricao(this.descricao)
                .valor(this.valor)
                .horarioFuncionamento(this.horarioFuncionamento)
                .observacoes(this.observacoes);
        
        return builder.build();
    }
}
