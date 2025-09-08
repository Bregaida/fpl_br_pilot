package br.com.fplbr.pilot.aerodromos.application.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Objects;

/**
 * DTO para resultados de busca de aerÃƒÂ³dromos no formulÃƒÂ¡rio de plano de voo.
 * ContÃƒÂ©m apenas as informaÃƒÂ§ÃƒÂµes essenciais para exibiÃƒÂ§ÃƒÂ£o na lista de sugestÃƒÂµes.
 */
@RegisterForReflection
public class AerodromoBuscaDTO {
    private String icao;
    private String iata;
    private String nome;
    private String municipio;
    private String uf;
    private String regiao;
    private boolean terminal;

    /**
     * Cria um DTO de busca a partir de um AerodromoDTO.
     */
    // ===== Constructors =====
    
    public AerodromoBuscaDTO() {
    }
    
    private AerodromoBuscaDTO(String icao, String iata, String nome, String municipio, 
                             String uf, String regiao, boolean terminal) {
        this.icao = icao;
        this.iata = iata;
        this.nome = nome;
        this.municipio = municipio;
        this.uf = uf;
        this.regiao = regiao;
        this.terminal = terminal;
    }
    
    // ===== Builder Pattern =====
    
    public static AerodromoBuscaDTOBuilder builder() {
        return new AerodromoBuscaDTOBuilder();
    }
    
    public static class AerodromoBuscaDTOBuilder {
        private String icao;
        private String iata;
        private String nome;
        private String municipio;
        private String uf;
        private String regiao;
        private boolean terminal;
        
        public AerodromoBuscaDTOBuilder icao(String icao) { this.icao = icao; return this; }
        public AerodromoBuscaDTOBuilder iata(String iata) { this.iata = iata; return this; }
        public AerodromoBuscaDTOBuilder nome(String nome) { this.nome = nome; return this; }
        public AerodromoBuscaDTOBuilder municipio(String municipio) { this.municipio = municipio; return this; }
        public AerodromoBuscaDTOBuilder uf(String uf) { this.uf = uf; return this; }
        public AerodromoBuscaDTOBuilder regiao(String regiao) { this.regiao = regiao; return this; }
        public AerodromoBuscaDTOBuilder terminal(boolean terminal) { this.terminal = terminal; return this; }
        
        public AerodromoBuscaDTO build() {
            return new AerodromoBuscaDTO(icao, iata, nome, municipio, uf, regiao, terminal);
        }
    }
    
    // ===== Static Factory Method =====
    
    public static AerodromoBuscaDTO fromAerodromoDTO(AerodromoDTO aerodromoDTO) {
        if (aerodromoDTO == null) {
            return null;
        }
        
        return builder()
                .icao(aerodromoDTO.getIcao())
                .iata(aerodromoDTO.getIata())
                .nome(aerodromoDTO.getNomeOficial())
                .municipio(aerodromoDTO.getMunicipio())
                .uf(aerodromoDTO.getUf())
                .regiao(aerodromoDTO.getRegiao())
                .terminal(Boolean.TRUE.equals(aerodromoDTO.getTerminal()))
                .build();
    }
    
    /**
     * Retorna uma representaÃƒÂ§ÃƒÂ£o em string formatada para exibiÃƒÂ§ÃƒÂ£o na interface.
     */
    // ===== Getters and Setters =====
    
    public String getIcao() { return icao; }
    public void setIcao(String icao) { this.icao = icao; }
    
    public String getIata() { return iata; }
    public void setIata(String iata) { this.iata = iata; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    
    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }
    
    public boolean isTerminal() { return terminal; }
    public void setTerminal(boolean terminal) { this.terminal = terminal; }
    
    // ===== equals, hashCode, and toString =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AerodromoBuscaDTO that = (AerodromoBuscaDTO) o;
        return terminal == that.terminal &&
               Objects.equals(icao, that.icao) &&
               Objects.equals(iata, that.iata) &&
               Objects.equals(nome, that.nome) &&
               Objects.equals(municipio, that.municipio) &&
               Objects.equals(uf, that.uf) &&
               Objects.equals(regiao, that.regiao);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(icao, iata, nome, municipio, uf, regiao, terminal);
    }
    
    @Override
    public String toString() {
        return "AerodromoBuscaDTO{" +
               "icao='" + icao + '\'' +
               ", iata='" + iata + '\'' +
               ", nome='" + nome + '\'' +
               ", municipio='" + municipio + '\'' +
               ", uf='" + uf + '\'' +
               ", regiao='" + regiao + '\'' +
               ", terminal=" + terminal +
               '}';
    }
    
    // ===== Display Methods =====
    
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(icao);
        
        if (iata != null && !iata.trim().isEmpty()) {
            sb.append(" / ").append(iata);
        }
        
        sb.append(" - ").append(nome);
        
        if (municipio != null && !municipio.trim().isEmpty()) {
            sb.append(", ").append(municipio);
        }
        
        if (uf != null && !uf.trim().isEmpty()) {
            sb.append("/").append(uf);
        }
        
        return sb.toString();
    }
}
