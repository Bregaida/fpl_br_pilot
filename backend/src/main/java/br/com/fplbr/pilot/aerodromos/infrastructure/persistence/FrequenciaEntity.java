package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import br.com.fplbr.pilot.aerodromos.domain.model.Frequencia;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidade JPA que representa uma frequência de rádio de aeródromo no banco de dados.
 */
@Entity
@Table(name = "frequencias")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FrequenciaEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aerodromo_icao", nullable = false)
    private AerodromoEntity aerodromo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoFrequencia tipo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "valor", nullable = false, length = 20)
    private String valor;

    @Column(name = "horario_funcionamento", length = 100)
    private String horarioFuncionamento;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    /**
     * Enumeração para os tipos de frequência.
     * Mantido aqui para mapeamento JPA, já que não podemos usar diretamente o enum do domínio.
     */
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

    /**
     * Converte esta entidade para o domínio Frequencia.
     */
    public Frequencia toDomain() {
        Frequencia.TipoFrequencia tipoFrequencia = null;
        if (this.tipo != null) {
            try {
                tipoFrequencia = Frequencia.TipoFrequencia.valueOf(this.tipo.name());
            } catch (IllegalArgumentException e) {
                // Se o tipo não for encontrado, mantém como null
            }
        }
        
        return Frequencia.builder()
                .tipo(tipoFrequencia)
                .descricao(this.descricao)
                .valor(this.valor)
                .horarioFuncionamento(this.horarioFuncionamento)
                .observacoes(this.observacoes)
                .build();
    }

    /**
     * Cria uma nova entidade a partir de um domínio Frequencia.
     *
     * @param frequencia O objeto de domínio Frequencia
     * @param aerodromo A entidade AerodromoEntity relacionada
     * @return Uma nova instância de FrequenciaEntity
     */
    public static FrequenciaEntity fromDomain(Frequencia frequencia, AerodromoEntity aerodromo) {
        if (frequencia == null) {
            return null;
        }

        TipoFrequencia tipo = null;
        if (frequencia.getTipo() != null) {
            try {
                tipo = TipoFrequencia.valueOf(frequencia.getTipo().name());
            } catch (IllegalArgumentException e) {
                // Se o tipo não for encontrado, mantém como null
            }
        }

        FrequenciaEntity entity = new FrequenciaEntity();
        entity.setDescricao(frequencia.getDescricao());
        entity.setValor(frequencia.getValor());
        entity.setHorarioFuncionamento(frequencia.getHorarioFuncionamento());
        entity.setObservacoes(frequencia.getObservacoes());
        entity.setTipo(tipo);
        entity.setAerodromo(aerodromo);
        return entity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AerodromoEntity getAerodromo() {
        return aerodromo;
    }

    public void setAerodromo(AerodromoEntity aerodromo) {
        this.aerodromo = aerodromo;
    }

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
