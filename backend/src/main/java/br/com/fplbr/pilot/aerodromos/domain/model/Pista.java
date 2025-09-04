package br.com.fplbr.pilot.aerodromos.domain.model;

import java.util.Objects;

/**
 * Representa uma pista de pouso e decolagem em um aeródromo.
 */
public class Pista {
    private String designacao;          // Ex: 10/28, 15/33
    private Double comprimentoMetros;   // Comprimento em metros
    private Double larguraMetros;       // Largura em metros
    private String superficie;          // Tipo de superfície (ASPH, CONC, GRVL, etc)
    private Integer resistenciaPcn;     // Número PCN da pista
    private String classificacaoPcn;    // Classificação PCN (A/B/C/D, Alto/Baixo, etc)
    private Integer tora;               // TORA - Take-Off Run Available
    private Integer toda;               // TODA - Take-Off Distance Available
    private Integer asda;               // ASDA - Accelerate-Stop Distance Available
    private Integer lda;                // LDA - Landing Distance Available
    private boolean ils;                // Se possui ILS
    private String categoriaIls;        // Categoria do ILS (I, II, III)
    private String papi;                // Tipo de PAPI (PAPI, APAPI, etc)
    private String luzesBorda;          // Tipo de iluminação de borda
    private String luzesCentro;         // Tipo de iluminação de centro
    private String observacoes;        // Observações adicionais

    // Private constructor for builder
    private Pista(Builder builder) {
        this.designacao = builder.designacao;
        this.comprimentoMetros = builder.comprimentoMetros;
        this.larguraMetros = builder.larguraMetros;
        this.superficie = builder.superficie;
        this.resistenciaPcn = builder.resistenciaPcn;
        this.classificacaoPcn = builder.classificacaoPcn;
        this.tora = builder.tora;
        this.toda = builder.toda;
        this.asda = builder.asda;
        this.lda = builder.lda;
        this.ils = builder.ils;
        this.categoriaIls = builder.categoriaIls;
        this.papi = builder.papi;
        this.luzesBorda = builder.luzesBorda;
        this.luzesCentro = builder.luzesCentro;
        this.observacoes = builder.observacoes;
    }

    // Default constructor
    public Pista() {
    }

    // Builder class
    public static class Builder {
        // Required parameters
        private final String designacao;

        // Optional parameters - initialized to default values
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

        public Builder(String designacao) {
            this.designacao = designacao;
        }

        public Builder comprimentoMetros(Double val) { comprimentoMetros = val; return this; }
        public Builder larguraMetros(Double val) { larguraMetros = val; return this; }
        public Builder superficie(String val) { superficie = val; return this; }
        public Builder resistenciaPcn(Integer val) { resistenciaPcn = val; return this; }
        public Builder classificacaoPcn(String val) { classificacaoPcn = val; return this; }
        public Builder tora(Integer val) { tora = val; return this; }
        public Builder toda(Integer val) { toda = val; return this; }
        public Builder asda(Integer val) { asda = val; return this; }
        public Builder lda(Integer val) { lda = val; return this; }
        public Builder ils(boolean val) { ils = val; return this; }
        public Builder categoriaIls(String val) { categoriaIls = val; return this; }
        public Builder papi(String val) { papi = val; return this; }
        public Builder luzesBorda(String val) { luzesBorda = val; return this; }
        public Builder luzesCentro(String val) { luzesCentro = val; return this; }
        public Builder observacoes(String val) { observacoes = val; return this; }

        public Pista build() {
            return new Pista(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pista pista = (Pista) o;
        return Objects.equals(designacao, pista.designacao) &&
                Objects.equals(comprimentoMetros, pista.comprimentoMetros) &&
                Objects.equals(larguraMetros, pista.larguraMetros) &&
                Objects.equals(superficie, pista.superficie) &&
                Objects.equals(resistenciaPcn, pista.resistenciaPcn) &&
                Objects.equals(classificacaoPcn, pista.classificacaoPcn) &&
                Objects.equals(tora, pista.tora) &&
                Objects.equals(toda, pista.toda) &&
                Objects.equals(asda, pista.asda) &&
                Objects.equals(lda, pista.lda) &&
                Objects.equals(ils, pista.ils) &&
                Objects.equals(categoriaIls, pista.categoriaIls) &&
                Objects.equals(papi, pista.papi) &&
                Objects.equals(luzesBorda, pista.luzesBorda) &&
                Objects.equals(luzesCentro, pista.luzesCentro) &&
                Objects.equals(observacoes, pista.observacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(designacao, comprimentoMetros, larguraMetros, superficie, resistenciaPcn, 
                classificacaoPcn, tora, toda, asda, lda, ils, categoriaIls, papi, luzesBorda, luzesCentro, observacoes);
    }

    // Getters and Setters
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
