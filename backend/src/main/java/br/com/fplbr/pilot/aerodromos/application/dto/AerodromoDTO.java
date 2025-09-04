package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para representação de um aeródromo na camada de apresentação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AerodromoDTO {
    private String icao;
    private String iata;
    private String nome;
    private String municipio;
    private String uf;
    private String regiao;
    private Double latitude;
    private Double longitude;
    private Integer altitudePes;
    private String tipo;
    private String uso;
    private String cindacta;
    private Boolean internacional;
    private Boolean terminal;
    private String horarioFuncionamento;
    private String telefone;
    private String email;
    private String responsavel;
    private List<PistaDTO> pistas;
    private List<FrequenciaDTO> frequencias;
    private String observacoes;
    private Boolean ativo;

    /**
     * Converte um Aerodromo (entidade de domínio) para AerodromoDTO.
     */
    public static AerodromoDTO fromDomain(Aerodromo aerodromo) {
        if (aerodromo == null) {
            return null;
        }

        return AerodromoDTO.builder()
                .icao(aerodromo.getIcao())
                .iata(aerodromo.getIata())
                .nome(aerodromo.getNome())
                .municipio(aerodromo.getMunicipio())
                .uf(aerodromo.getUf())
                .regiao(aerodromo.getRegiao())
                .latitude(aerodromo.getLatitude())
                .longitude(aerodromo.getLongitude())
                .altitudePes(aerodromo.getAltitudePes())
                .tipo(aerodromo.getTipo())
                .uso(aerodromo.getUso())
                .cindacta(aerodromo.getCindacta())
                .internacional(aerodromo.isInternacional())
                .terminal(aerodromo.isTerminal())
                .horarioFuncionamento(aerodromo.getHorarioFuncionamento())
                .telefone(aerodromo.getTelefone())
                .email(aerodromo.getEmail())
                .responsavel(aerodromo.getResponsavel())
                .pistas(aerodromo.getPistas() != null ? 
                        aerodromo.getPistas().stream()
                                .map(PistaDTO::fromDomain)
                                .collect(Collectors.toList()) : null)
                .frequencias(aerodromo.getFrequencias() != null ? 
                        aerodromo.getFrequencias().stream()
                                .map(FrequenciaDTO::fromDomain)
                                .collect(Collectors.toList()) : null)
                .observacoes(aerodromo.getObservacoes())
                .ativo(aerodromo.isAtivo())
                .build();
    }

    /**
     * Converte este DTO para uma entidade de domínio Aerodromo.
     */
    public Aerodromo toDomain() {
        return Aerodromo.builder()
                .icao(this.icao)
                .iata(this.iata)
                .nome(this.nome)
                .municipio(this.municipio)
                .uf(this.uf)
                .regiao(this.regiao)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .altitudePes(this.altitudePes)
                .tipo(this.tipo)
                .uso(this.uso)
                .cindacta(this.cindacta)
                .internacional(this.internacional != null && this.internacional)
                .terminal(this.terminal != null && this.terminal)
                .horarioFuncionamento(this.horarioFuncionamento)
                .telefone(this.telefone)
                .email(this.email)
                .responsavel(this.responsavel)
                .pistas(this.pistas != null ? 
                        this.pistas.stream()
                                .map(PistaDTO::toDomain)
                                .collect(Collectors.toList()) : null)
                .frequencias(this.frequencias != null ? 
                        this.frequencias.stream()
                                .map(FrequenciaDTO::toDomain)
                                .collect(Collectors.toList()) : null)
                .observacoes(this.observacoes)
                .ativo(this.ativo != null && this.ativo)
                .build();
    }
}
