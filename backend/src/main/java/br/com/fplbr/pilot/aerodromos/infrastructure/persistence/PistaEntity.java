package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Entidade JPA que representa uma pista de aeródromo no banco de dados.
 */
@Entity
@Table(name = "pistas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PistaEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aerodromo_icao", nullable = false)
    private AerodromoEntity aerodromo;

    @Column(name = "designacao", nullable = false, length = 50)
    private String designacao;

    @Column(name = "comprimento_metros")
    private Double comprimentoMetros;

    @Column(name = "largura_metros")
    private Double larguraMetros;

    @Column(name = "superficie", length = 50)
    private String superficie;

    @Column(name = "resistencia_pcn")
    private Integer resistenciaPcn;

    @Column(name = "classificacao_pcn", length = 20)
    private String classificacaoPcn;

    @Column(name = "tora")
    private Integer tora;

    @Column(name = "toda")
    private Integer toda;

    @Column(name = "asda")
    private Integer asda;

    @Column(name = "lda")
    private Integer lda;

    @Column(name = "ils")
    private boolean ils;

    @Column(name = "categoria_ils", length = 10)
    private String categoriaIls;

    @Column(name = "papi", length = 20)
    private String papi;

    @Column(name = "luzes_borda", length = 50)
    private String luzesBorda;

    @Column(name = "luzes_centro", length = 50)
    private String luzesCentro;

    @Column(name = "observacoes", length = 2000)
    private String observacoes;


    /**
     * Cria uma nova instância de PistaEntity a partir de um objeto de domínio Pista.
     */
    public static PistaEntity fromDomain(Pista pista, AerodromoEntity aerodromoEntity) {
        return new PistaEntity.Builder(aerodromoEntity, pista.getDesignacao())
                .comprimentoMetros(pista.getComprimentoMetros())
                .larguraMetros(pista.getLarguraMetros())
                .superficie(pista.getSuperficie())
                .resistenciaPcn(pista.getResistenciaPcn())
                .classificacaoPcn(pista.getClassificacaoPcn())
                .tora(pista.getTora())
                .toda(pista.getToda())
                .asda(pista.getAsda())
                .lda(pista.getLda())
                .ils(pista.isIls())
                .categoriaIls(pista.getCategoriaIls())
                .papi(pista.getPapi())
                .luzesBorda(pista.getLuzesBorda())
                .luzesCentro(pista.getLuzesCentro())
                .observacoes(pista.getObservacoes())
                .build();
    }

    /**
     * Converte esta entidade para o domínio Pista.
     */
    public Pista toDomain() {
        return new Pista.Builder(this.designacao)
                .comprimentoMetros(this.comprimentoMetros)
                .larguraMetros(this.larguraMetros)
                .superficie(this.superficie)
                .resistenciaPcn(this.resistenciaPcn)
                .classificacaoPcn(this.classificacaoPcn)
                .tora(this.tora)
                .toda(this.toda)
                .asda(this.asda)
                .lda(this.lda)
                .ils(this.ils)
                .categoriaIls(this.categoriaIls)
                .papi(this.papi)
                .luzesBorda(this.luzesBorda)
                .luzesCentro(this.luzesCentro)
                .observacoes(this.observacoes)
                .build();
    }

     * @param pista O objeto de domínio Pista
     * @param aerodromo A entidade AerodromoEntity relacionada
     * @return Uma nova instância de PistaEntity
     */
    public static PistaEntity fromDomain(Pista pista, AerodromoEntity aerodromo) {
        if (pista == null) {
            return null;
        }

        PistaEntity entity = new PistaEntity();
        entity.setDesignacao(pista.getDesignacao());
        entity.setComprimentoMetros(pista.getComprimentoMetros());
        entity.setLarguraMetros(pista.getLarguraMetros());
        entity.setSuperficie(pista.getSuperficie());
        entity.setResistenciaPcn(pista.getResistenciaPcn());
        entity.setClassificacaoPcn(pista.getClassificacaoPcn());
        entity.setTora(pista.getTora());
        entity.setToda(pista.getToda());
        entity.setAsda(pista.getAsda());
        entity.setLda(pista.getLda());
        entity.setIls(pista.isIls());
        entity.setCategoriaIls(pista.getCategoriaIls());
        entity.setPapi(pista.getPapi());
        entity.setLuzesBorda(pista.getLuzesBorda());
        entity.setLuzesCentro(pista.getLuzesCentro());
        entity.setObservacoes(pista.getObservacoes());
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

    public String getDesignacao() {
        return designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public Double getComprimentoMetros() {
        return comprimentoMetros;
    }

    public void setComprimentoMetros(Double comprimentoMetros) {
        this.comprimentoMetros = comprimentoMetros;
    }

    public Double getLarguraMetros() {
        return larguraMetros;
    }

    public void setLarguraMetros(Double larguraMetros) {
        this.larguraMetros = larguraMetros;
    }

    public String getSuperficie() {
        return superficie;
    }

    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }

    public Integer getResistenciaPcn() {
        return resistenciaPcn;
    }

    public void setResistenciaPcn(Integer resistenciaPcn) {
        this.resistenciaPcn = resistenciaPcn;
    }

    public String getClassificacaoPcn() {
        return classificacaoPcn;
    }

    public void setClassificacaoPcn(String classificacaoPcn) {
        this.classificacaoPcn = classificacaoPcn;
    }

    public Integer getTora() {
        return tora;
    }

    public void setTora(Integer tora) {
        this.tora = tora;
    }

    public Integer getToda() {
        return toda;
    }

    public void setToda(Integer toda) {
        this.toda = toda;
    }

    public Integer getAsda() {
        return asda;
    }

    public void setAsda(Integer asda) {
        this.asda = asda;
    }

    public Integer getLda() {
        return lda;
    }

    public void setLda(Integer lda) {
        this.lda = lda;
    }

    public boolean isIls() {
        return ils;
    }

    public void setIls(boolean ils) {
        this.ils = ils;
    }

    public String getCategoriaIls() {
        return categoriaIls;
    }

    public void setCategoriaIls(String categoriaIls) {
        this.categoriaIls = categoriaIls;
    }

    public String getPapi() {
        return papi;
    }

    public void setPapi(String papi) {
        this.papi = papi;
    }

    public String getLuzesBorda() {
        return luzesBorda;
    }

    public void setLuzesBorda(String luzesBorda) {
        this.luzesBorda = luzesBorda;
    }

    public String getLuzesCentro() {
        return luzesCentro;
    }

    public void setLuzesCentro(String luzesCentro) {
        this.luzesCentro = luzesCentro;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
