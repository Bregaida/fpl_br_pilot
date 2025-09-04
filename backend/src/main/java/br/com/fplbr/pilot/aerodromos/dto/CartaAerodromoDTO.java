package br.com.fplbr.pilot.aerodromos.dto;

import java.time.LocalDate;
import java.util.Objects;
public class CartaAerodromoDTO {
    private String icao;         // ex: SBSP
    private String titulo;       // ex: AD 2 SBSP IAC RWY 17R
    private String tipo;         // IAC, SID, STAR, VAC, ADC, etc (inferido do nome)
    private String caminho;      // caminho local (filesystem) para o PDF
    private String hash;         // sha-256 para dedupe
    private LocalDate ciclo;     // se conseguirmos inferir do nome (opcional)

    // Private constructor for builder
    private CartaAerodromoDTO(Builder builder) {
        this.icao = builder.icao;
        this.titulo = builder.titulo;
        this.tipo = builder.tipo;
        this.caminho = builder.caminho;
        this.hash = builder.hash;
        this.ciclo = builder.ciclo;
    }

    // Default constructor
    public CartaAerodromoDTO() {
    }

    // Builder class
    public static class Builder {
        // Required parameters
        private final String icao;
        private final String titulo;
        private final String tipo;

        // Optional parameters - initialized to default values
        private String caminho;
        private String hash;
        private LocalDate ciclo;

        public Builder(String icao, String titulo, String tipo) {
            this.icao = icao;
            this.titulo = titulo;
            this.tipo = tipo;
        }

        public Builder caminho(String val) { caminho = val; return this; }
        public Builder hash(String val) { hash = val; return this; }
        public Builder ciclo(LocalDate val) { ciclo = val; return this; }

        public CartaAerodromoDTO build() {
            return new CartaAerodromoDTO(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaAerodromoDTO that = (CartaAerodromoDTO) o;
        return Objects.equals(icao, that.icao) &&
                Objects.equals(titulo, that.titulo) &&
                Objects.equals(tipo, that.tipo) &&
                Objects.equals(caminho, that.caminho) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(ciclo, that.ciclo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icao, titulo, tipo, caminho, hash, ciclo);
    }

    // Getters and Setters
    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDate getCiclo() {
        return ciclo;
    }

    public void setCiclo(LocalDate ciclo) {
        this.ciclo = ciclo;
    }
}
