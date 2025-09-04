package br.com.fplbr.pilot.aerodromos.application.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resultados de busca de aeródromos no formulário de plano de voo.
 * Contém apenas as informações essenciais para exibição na lista de sugestões.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    public static AerodromoBuscaDTO fromAerodromoDTO(AerodromoDTO aerodromoDTO) {
        if (aerodromoDTO == null) {
            return null;
        }
        
        return AerodromoBuscaDTO.builder()
                .icao(aerodromoDTO.getIcao())
                .iata(aerodromoDTO.getIata())
                .nome(aerodromoDTO.getNome())
                .municipio(aerodromoDTO.getMunicipio())
                .uf(aerodromoDTO.getUf())
                .regiao(aerodromoDTO.getRegiao())
                .terminal(aerodromoDTO.getTerminal() != null && aerodromoDTO.getTerminal())
                .build();
    }
    
    /**
     * Retorna uma representação em string formatada para exibição na interface.
     */
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
