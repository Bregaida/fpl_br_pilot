package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a runway (pista) in the database.
 * Maps to the 'pistas' table.
 */
@Entity
@Table(name = "pistas")
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
     * Finds a PistaEntity by its ID.
     *
     * @param id the ID of the pista to find
     * @return an Optional containing the found PistaEntity, or empty if not found
     */
    public static Optional<PistaEntity> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    /**
     * Converts this entity to a domain Pista object.
     *
     * @return a new Pista domain object
     */
    public Pista toDomain() {
        if (this.designacao == null) {
            throw new IllegalStateException("DesignaÃƒÂ§ÃƒÂ£o da pista nÃƒÂ£o pode ser nula");
        }
        
        return Pista.builder()
            .designacao(this.designacao)
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

    /**
     * Creates a new PistaEntity from a domain Pista object.
     *
     * @param pista the domain Pista object
     * @param aerodromo the AerodromoEntity this pista belongs to
     * @return a new PistaEntity instance
     */
    public static PistaEntity fromDomain(Pista pista, AerodromoEntity aerodromo) {
        if (pista == null) {
            return null;
        }

        return builder()
            .designacao(pista.getDesignacao())
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
            .aerodromo(aerodromo)
            .build();
    }

    // ======================================
    // Getters and Setters
    // ======================================

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

    // ======================================
    // Builder Pattern Implementation
    // ======================================
    
    public static PistaEntityBuilder builder() {
        return new PistaEntityBuilder();
    }

    public static final class PistaEntityBuilder {
        private Long id;
        private AerodromoEntity aerodromo;
        private String designacao;
        private Double comprimentoMetros;
        private Double larguraMetros;
        private String superficie;
        private Integer resistenciaPcn;
        private String classificacaoPcn;
        private Integer tora;
        private Integer toda;
        private Integer asda;
        private Integer lda;
        private boolean ils;
        private String categoriaIls;
        private String papi;
        private String luzesBorda;
        private String luzesCentro;
        private String observacoes;

        private PistaEntityBuilder() {}

        public PistaEntityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PistaEntityBuilder aerodromo(AerodromoEntity aerodromo) {
            this.aerodromo = aerodromo;
            return this;
        }

        public PistaEntityBuilder designacao(String designacao) {
            this.designacao = designacao;
            return this;
        }

        public PistaEntityBuilder comprimentoMetros(Double comprimentoMetros) {
            this.comprimentoMetros = comprimentoMetros;
            return this;
        }

        public PistaEntityBuilder larguraMetros(Double larguraMetros) {
            this.larguraMetros = larguraMetros;
            return this;
        }

        public PistaEntityBuilder superficie(String superficie) {
            this.superficie = superficie;
            return this;
        }

        public PistaEntityBuilder resistenciaPcn(Integer resistenciaPcn) {
            this.resistenciaPcn = resistenciaPcn;
            return this;
        }

        public PistaEntityBuilder classificacaoPcn(String classificacaoPcn) {
            this.classificacaoPcn = classificacaoPcn;
            return this;
        }

        public PistaEntityBuilder tora(Integer tora) {
            this.tora = tora;
            return this;
        }

        public PistaEntityBuilder toda(Integer toda) {
            this.toda = toda;
            return this;
        }

        public PistaEntityBuilder asda(Integer asda) {
            this.asda = asda;
            return this;
        }

        public PistaEntityBuilder lda(Integer lda) {
            this.lda = lda;
            return this;
        }

        public PistaEntityBuilder ils(boolean ils) {
            this.ils = ils;
            return this;
        }

        public PistaEntityBuilder categoriaIls(String categoriaIls) {
            this.categoriaIls = categoriaIls;
            return this;
        }

        public PistaEntityBuilder papi(String papi) {
            this.papi = papi;
            return this;
        }

        public PistaEntityBuilder luzesBorda(String luzesBorda) {
            this.luzesBorda = luzesBorda;
            return this;
        }

        public PistaEntityBuilder luzesCentro(String luzesCentro) {
            this.luzesCentro = luzesCentro;
            return this;
        }

        public PistaEntityBuilder observacoes(String observacoes) {
            this.observacoes = observacoes;
            return this;
        }

        public PistaEntity build() {
            PistaEntity entity = new PistaEntity();
            entity.setId(id);
            entity.setAerodromo(aerodromo);
            entity.setDesignacao(designacao);
            entity.setComprimentoMetros(comprimentoMetros);
            entity.setLarguraMetros(larguraMetros);
            entity.setSuperficie(superficie);
            entity.setResistenciaPcn(resistenciaPcn);
            entity.setClassificacaoPcn(classificacaoPcn);
            entity.setTora(tora);
            entity.setToda(toda);
            entity.setAsda(asda);
            entity.setLda(lda);
            entity.setIls(ils);
            entity.setCategoriaIls(categoriaIls);
            entity.setPapi(papi);
            entity.setLuzesBorda(luzesBorda);
            entity.setLuzesCentro(luzesCentro);
            entity.setObservacoes(observacoes);
            return entity;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PistaEntity that = (PistaEntity) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(designacao, that.designacao) &&
               Objects.equals(aerodromo, that.aerodromo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, designacao, aerodromo);
    }

    @Override
    public String toString() {
        return "PistaEntity{" +
               "id=" + id +
               ", designacao='" + designacao + '\'' +
               ", aerodromo=" + (aerodromo != null ? aerodromo.getIcao() : "null") +
               '}';
    }
}
