package br.com.fplbr.pilot.aerodromos.domain.model;

import java.util.Objects;

/**
 * Representa uma frequência de rádio disponível em um aeródromo.
 */
public class Frequencia {
    public enum TipoFrequencia {
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

    // Builder pattern implementation
    public static FrequenciaBuilder builder() {
        return new FrequenciaBuilder();
    }
    
    public static class FrequenciaBuilder {
        private TipoFrequencia tipo;
        private String descricao;
        private String valor;
        private String horarioFuncionamento;
        private String observacoes;
        
        public FrequenciaBuilder tipo(TipoFrequencia tipo) { this.tipo = tipo; return this; }
        public FrequenciaBuilder descricao(String descricao) { this.descricao = descricao; return this; }
        public FrequenciaBuilder valor(String valor) { this.valor = valor; return this; }
        public FrequenciaBuilder horarioFuncionamento(String horarioFuncionamento) { 
            this.horarioFuncionamento = horarioFuncionamento; 
            return this; 
        }
        public FrequenciaBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }
        
        public Frequencia build() {
            return new Frequencia(tipo, descricao, valor, horarioFuncionamento, observacoes);
        }
    }
    
    // Fields with JavaDoc
    private final TipoFrequencia tipo;    // Tipo de frequência
    private final String descricao;       // Descrição da frequência (ex: "Torre Principal")
    private final String valor;           // Valor da frequência (ex: "118.10")
    private String horarioFuncionamento;  // Horário de funcionamento (ex: "24H", "HORÁRIO DE VOO")
    private String observacoes;           // Observações adicionais
    
    // Constructor
    public Frequencia(
        TipoFrequencia tipo, 
        String descricao, 
        String valor, 
        String horarioFuncionamento, 
        String observacoes
    ) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.horarioFuncionamento = horarioFuncionamento;
        this.observacoes = observacoes;
    }
    
    // No-args constructor for frameworks
    protected Frequencia() {
        this.tipo = null;
        this.descricao = null;
        this.valor = null;
        this.horarioFuncionamento = null;
        this.observacoes = null;
    }
    
    // toBuilder method
    public FrequenciaBuilder toBuilder() {
        return new FrequenciaBuilder()
            .tipo(this.tipo)
            .descricao(this.descricao)
            .valor(this.valor)
            .horarioFuncionamento(this.horarioFuncionamento)
            .observacoes(this.observacoes);
    }
    
    // Getters
    public TipoFrequencia getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public String getValor() { return valor; }
    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public String getObservacoes() { return observacoes; }
    
    // Setters for mutable fields
    public void setHorarioFuncionamento(String horarioFuncionamento) { 
        this.horarioFuncionamento = horarioFuncionamento; 
    }
    
    public void setObservacoes(String observacoes) { 
        this.observacoes = observacoes; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frequencia that = (Frequencia) o;
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
        return "Frequencia{" +
                "tipo=" + tipo +
                ", descricao='" + descricao + '\'' +
                ", valor='" + valor + '\'' +
                ", horarioFuncionamento='" + horarioFuncionamento + '\'' +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
