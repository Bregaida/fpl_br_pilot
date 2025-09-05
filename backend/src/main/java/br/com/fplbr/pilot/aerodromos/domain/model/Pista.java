package br.com.fplbr.pilot.aerodromos.domain.model;

import java.util.Objects;

/**
 * Representa uma pista de pouso e decolagem em um aeródromo.
 */
public class Pista {
    // Builder pattern implementation
    public static PistaBuilder builder() {
        return new PistaBuilder();
    }
    
    public static class PistaBuilder {
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
        
        public PistaBuilder designacao(String designacao) { this.designacao = designacao; return this; }
        public PistaBuilder comprimentoMetros(Double comprimentoMetros) { this.comprimentoMetros = comprimentoMetros; return this; }
        public PistaBuilder larguraMetros(Double larguraMetros) { this.larguraMetros = larguraMetros; return this; }
        public PistaBuilder superficie(String superficie) { this.superficie = superficie; return this; }
        public PistaBuilder resistenciaPcn(Integer resistenciaPcn) { this.resistenciaPcn = resistenciaPcn; return this; }
        public PistaBuilder classificacaoPcn(String classificacaoPcn) { this.classificacaoPcn = classificacaoPcn; return this; }
        public PistaBuilder tora(Integer tora) { this.tora = tora; return this; }
        public PistaBuilder toda(Integer toda) { this.toda = toda; return this; }
        public PistaBuilder asda(Integer asda) { this.asda = asda; return this; }
        public PistaBuilder lda(Integer lda) { this.lda = lda; return this; }
        public PistaBuilder ils(boolean ils) { this.ils = ils; return this; }
        public PistaBuilder categoriaIls(String categoriaIls) { this.categoriaIls = categoriaIls; return this; }
        public PistaBuilder papi(String papi) { this.papi = papi; return this; }
        public PistaBuilder luzesBorda(String luzesBorda) { this.luzesBorda = luzesBorda; return this; }
        public PistaBuilder luzesCentro(String luzesCentro) { this.luzesCentro = luzesCentro; return this; }
        public PistaBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }
        
        public Pista build() {
            return new Pista(
                designacao, comprimentoMetros, larguraMetros, superficie, resistenciaPcn,
                classificacaoPcn, tora, toda, asda, lda, ils, categoriaIls, papi,
                luzesBorda, luzesCentro, observacoes
            );
        }
    }
    
    // Fields with JavaDoc
    private final String designacao;          // Ex: 10/28, 15/33
    private final Double comprimentoMetros;   // Comprimento em metros
    private final Double larguraMetros;       // Largura em metros
    private final String superficie;          // Tipo de superfície (ASPH, CONC, GRVL, etc)
    private final Integer resistenciaPcn;     // Número PCN da pista
    private final String classificacaoPcn;    // Classificação PCN (A/B/C/D, Alto/Baixo, etc)
    private final Integer tora;               // TORA - Take-Off Run Available
    private final Integer toda;               // TODA - Take-Off Distance Available
    private final Integer asda;               // ASDA - Accelerate-Stop Distance Available
    private final Integer lda;                // LDA - Landing Distance Available
    private final boolean ils;                // Se possui ILS
    private final String categoriaIls;        // Categoria do ILS (I, II, III)
    private final String papi;                // Tipo de PAPI (PAPI, APAPI, etc)
    private final String luzesBorda;          // Tipo de iluminação de borda
    private final String luzesCentro;         // Tipo de iluminação de centro
    private String observacoes;               // Observações adicionais
    
    // Constructor
    public Pista(
        String designacao, Double comprimentoMetros, Double larguraMetros, 
        String superficie, Integer resistenciaPcn, String classificacaoPcn,
        Integer tora, Integer toda, Integer asda, Integer lda, boolean ils,
        String categoriaIls, String papi, String luzesBorda, String luzesCentro,
        String observacoes
    ) {
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
    
    // No-args constructor for frameworks
    protected Pista() {
        this.designacao = null;
        this.comprimentoMetros = null;
        this.larguraMetros = null;
        this.superficie = null;
        this.resistenciaPcn = null;
        this.classificacaoPcn = null;
        this.tora = null;
        this.toda = null;
        this.asda = null;
        this.lda = null;
        this.ils = false;
        this.categoriaIls = null;
        this.papi = null;
        this.luzesBorda = null;
        this.luzesCentro = null;
        this.observacoes = null;
    }
    // toBuilder method
    public PistaBuilder toBuilder() {
        return new PistaBuilder()
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
            .observacoes(this.observacoes);
    }
    
    // Getters
    public String getDesignacao() { return designacao; }
    public Double getComprimentoMetros() { return comprimentoMetros; }
    public Double getLarguraMetros() { return larguraMetros; }
    public String getSuperficie() { return superficie; }
    public Integer getResistenciaPcn() { return resistenciaPcn; }
    public String getClassificacaoPcn() { return classificacaoPcn; }
    public Integer getTora() { return tora; }
    public Integer getToda() { return toda; }
    public Integer getAsda() { return asda; }
    public Integer getLda() { return lda; }
    public boolean isIls() { return ils; }
    public String getCategoriaIls() { return categoriaIls; }
    public String getPapi() { return papi; }
    public String getLuzesBorda() { return luzesBorda; }
    public String getLuzesCentro() { return luzesCentro; }
    public String getObservacoes() { return observacoes; }
    
    // Setters for mutable fields
    public void setObservacoes(String observacoes) { 
        this.observacoes = observacoes; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pista pista = (Pista) o;
        return ils == pista.ils &&
                Objects.equals(designacao, pista.designacao) &&
                Objects.equals(comprimentoMetros, pista.comprimentoMetros) &&
                Objects.equals(larguraMetros, pista.larguraMetros) &&
                Objects.equals(superficie, pista.superficie) &&
                Objects.equals(resistenciaPcn, pista.resistenciaPcn) &&
                Objects.equals(classificacaoPcn, pista.classificacaoPcn) &&
                Objects.equals(tora, pista.tora) &&
                Objects.equals(toda, pista.toda) &&
                Objects.equals(asda, pista.asda) &&
                Objects.equals(lda, pista.lda) &&
                Objects.equals(categoriaIls, pista.categoriaIls) &&
                Objects.equals(papi, pista.papi) &&
                Objects.equals(luzesBorda, pista.luzesBorda) &&
                Objects.equals(luzesCentro, pista.luzesCentro) &&
                Objects.equals(observacoes, pista.observacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(designacao, comprimentoMetros, larguraMetros, superficie,
                resistenciaPcn, classificacaoPcn, tora, toda, asda, lda, ils,
                categoriaIls, papi, luzesBorda, luzesCentro, observacoes);
    }

    @Override
    public String toString() {
        return "Pista{" +
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
}
