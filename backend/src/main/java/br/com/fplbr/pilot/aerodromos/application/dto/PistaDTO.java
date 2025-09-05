package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import java.util.Objects;

/**
 * DTO para representação de uma pista de aeródromo na camada de apresentação.
 */
public class PistaDTO {
    // Fields
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
    private Boolean ils;
    private String categoriaIls;
    private String papi;
    private String luzesBorda;
    private String luzesCentro;
    private String observacoes;

    // No-args constructor
    public PistaDTO() {
    }

    // All-args constructor for builder
    private PistaDTO(String designacao, Double comprimentoMetros, Double larguraMetros, 
                    String superficie, Integer resistenciaPcn, String classificacaoPcn, 
                    Integer tora, Integer toda, Integer asda, Integer lda, Boolean ils, 
                    String categoriaIls, String papi, String luzesBorda, String luzesCentro, 
                    String observacoes) {
        this.designacao = designacao;
        this.comprimentoMetros = comprimentoMetros;
        this.larguraMetros = larguraMetros;
        this.superficie = superficie;
        this.resistenciaPcn = resistenciaPcn;
        this.classificacaoPcn = classificacaoPcn;
        this.tora = tora;
        this.toda = toda;
        this.asda = asda;
        this.lda = lda;
        this.ils = ils;
        this.categoriaIls = categoriaIls;
        this.papi = papi;
        this.luzesBorda = luzesBorda;
        this.luzesCentro = luzesCentro;
        this.observacoes = observacoes;
    }

    // Builder pattern
    public static PistaDTOBuilder builder() {
        return new PistaDTOBuilder();
    }

    public static class PistaDTOBuilder {
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
        private Boolean ils;
        private String categoriaIls;
        private String papi;
        private String luzesBorda;
        private String luzesCentro;
        private String observacoes;

        public PistaDTOBuilder designacao(String designacao) { this.designacao = designacao; return this; }
        public PistaDTOBuilder comprimentoMetros(Double comprimentoMetros) { this.comprimentoMetros = comprimentoMetros; return this; }
        public PistaDTOBuilder larguraMetros(Double larguraMetros) { this.larguraMetros = larguraMetros; return this; }
        public PistaDTOBuilder superficie(String superficie) { this.superficie = superficie; return this; }
        public PistaDTOBuilder resistenciaPcn(Integer resistenciaPcn) { this.resistenciaPcn = resistenciaPcn; return this; }
        public PistaDTOBuilder classificacaoPcn(String classificacaoPcn) { this.classificacaoPcn = classificacaoPcn; return this; }
        public PistaDTOBuilder tora(Integer tora) { this.tora = tora; return this; }
        public PistaDTOBuilder toda(Integer toda) { this.toda = toda; return this; }
        public PistaDTOBuilder asda(Integer asda) { this.asda = asda; return this; }
        public PistaDTOBuilder lda(Integer lda) { this.lda = lda; return this; }
        public PistaDTOBuilder ils(Boolean ils) { this.ils = ils; return this; }
        public PistaDTOBuilder categoriaIls(String categoriaIls) { this.categoriaIls = categoriaIls; return this; }
        public PistaDTOBuilder papi(String papi) { this.papi = papi; return this; }
        public PistaDTOBuilder luzesBorda(String luzesBorda) { this.luzesBorda = luzesBorda; return this; }
        public PistaDTOBuilder luzesCentro(String luzesCentro) { this.luzesCentro = luzesCentro; return this; }
        public PistaDTOBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }

        public PistaDTO build() {
            return new PistaDTO(
                designacao, comprimentoMetros, larguraMetros, superficie, 
                resistenciaPcn, classificacaoPcn, tora, toda, asda, lda, ils, 
                categoriaIls, papi, luzesBorda, luzesCentro, observacoes
            );
        }
    }
    // Getters and Setters
    public String getDesignacao() { return designacao; }
    public void setDesignacao(String designacao) { this.designacao = designacao; }
    
    public Double getComprimentoMetros() { return comprimentoMetros; }
    public void setComprimentoMetros(Double comprimentoMetros) { this.comprimentoMetros = comprimentoMetros; }
    
    public Double getLarguraMetros() { return larguraMetros; }
    public void setLarguraMetros(Double larguraMetros) { this.larguraMetros = larguraMetros; }
    
    public String getSuperficie() { return superficie; }
    public void setSuperficie(String superficie) { this.superficie = superficie; }
    
    public Integer getResistenciaPcn() { return resistenciaPcn; }
    public void setResistenciaPcn(Integer resistenciaPcn) { this.resistenciaPcn = resistenciaPcn; }
    
    public String getClassificacaoPcn() { return classificacaoPcn; }
    public void setClassificacaoPcn(String classificacaoPcn) { this.classificacaoPcn = classificacaoPcn; }
    
    public Integer getTora() { return tora; }
    public void setTora(Integer tora) { this.tora = tora; }
    
    public Integer getToda() { return toda; }
    public void setToda(Integer toda) { this.toda = toda; }
    
    public Integer getAsda() { return asda; }
    public void setAsda(Integer asda) { this.asda = asda; }
    
    public Integer getLda() { return lda; }
    public void setLda(Integer lda) { this.lda = lda; }
    
    public Boolean isIls() { return ils; }
    public void setIls(Boolean ils) { this.ils = ils; }
    
    public String getCategoriaIls() { return categoriaIls; }
    public void setCategoriaIls(String categoriaIls) { this.categoriaIls = categoriaIls; }
    
    public String getPapi() { return papi; }
    public void setPapi(String papi) { this.papi = papi; }
    
    public String getLuzesBorda() { return luzesBorda; }
    public void setLuzesBorda(String luzesBorda) { this.luzesBorda = luzesBorda; }
    
    public String getLuzesCentro() { return luzesCentro; }
    public void setLuzesCentro(String luzesCentro) { this.luzesCentro = luzesCentro; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // equals, hashCode, and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PistaDTO pistaDTO = (PistaDTO) o;
        return Objects.equals(designacao, pistaDTO.designacao) &&
               Objects.equals(comprimentoMetros, pistaDTO.comprimentoMetros) &&
               Objects.equals(larguraMetros, pistaDTO.larguraMetros) &&
               Objects.equals(superficie, pistaDTO.superficie) &&
               Objects.equals(resistenciaPcn, pistaDTO.resistenciaPcn) &&
               Objects.equals(classificacaoPcn, pistaDTO.classificacaoPcn) &&
               Objects.equals(tora, pistaDTO.tora) &&
               Objects.equals(toda, pistaDTO.toda) &&
               Objects.equals(asda, pistaDTO.asda) &&
               Objects.equals(lda, pistaDTO.lda) &&
               Objects.equals(ils, pistaDTO.ils) &&
               Objects.equals(categoriaIls, pistaDTO.categoriaIls) &&
               Objects.equals(papi, pistaDTO.papi) &&
               Objects.equals(luzesBorda, pistaDTO.luzesBorda) &&
               Objects.equals(luzesCentro, pistaDTO.luzesCentro) &&
               Objects.equals(observacoes, pistaDTO.observacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(designacao, comprimentoMetros, larguraMetros, superficie, 
                          resistenciaPcn, classificacaoPcn, tora, toda, asda, lda, ils, 
                          categoriaIls, papi, luzesBorda, luzesCentro, observacoes);
    }

    @Override
    public String toString() {
        return "PistaDTO{" +
               "designacao='" + designacao + '\'' +
               ", comprimentoMetros=" + comprimentoMetros +
               ", larguraMetros=" + larguraMetros +
               ", superficie='" + superficie + '\'' +
               ", resistenciaPcn=" + resistenciaPcn +
               ", classificacaoPcn='" + classificacaoPcn + '\'' +
               ", tora=" + tora +
               ", toda=" + toda +
               ", asda=" + asda +
               ", lda=" + lda +
               ", ils=" + ils +
               ", categoriaIls='" + categoriaIls + '\'' +
               ", papi='" + papi + '\'' +
               ", luzesBorda='" + luzesBorda + '\'' +
               ", luzesCentro='" + luzesCentro + '\'' +
               ", observacoes='" + observacoes + '\'' +
               '}';
    }

    /**
     * Converte uma entidade Pista para PistaDTO.
     */
    public static PistaDTO fromDomain(br.com.fplbr.pilot.aerodromos.domain.model.Pista pista) {
        if (pista == null) {
            return null;
        }

        return new PistaDTOBuilder()
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
                .build();
    }

    /**
     * Converte este DTO para uma entidade de domínio Pista.
     */
    public br.com.fplbr.pilot.aerodromos.domain.model.Pista toDomain() {
        // Assuming Pista has a similar builder pattern implemented
        br.com.fplbr.pilot.aerodromos.domain.model.Pista.PistaBuilder builder = 
            br.com.fplbr.pilot.aerodromos.domain.model.Pista.builder()
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
                .ils(this.ils != null && this.ils)
                .categoriaIls(this.categoriaIls)
                .papi(this.papi)
                .luzesBorda(this.luzesBorda)
                .luzesCentro(this.luzesCentro)
                .observacoes(this.observacoes);
        
        return builder.build();
    }
}
