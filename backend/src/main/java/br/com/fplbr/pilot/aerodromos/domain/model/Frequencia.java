package br.com.fplbr.pilot.aerodromos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Representa uma frequência de rádio disponível em um aeródromo.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private TipoFrequencia tipo;    // Tipo de frequência
    private String descricao;       // Descrição da frequência (ex: "Torre Principal")
    private String valor;           // Valor da frequência (ex: "118.10")
    private String horarioFuncionamento; // Horário de funcionamento (ex: "24H", "HORÁRIO DE VOO")
    private String observacoes;     // Observações adicionais

    // Getters and Setters
    public TipoFrequencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoFrequencia tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
