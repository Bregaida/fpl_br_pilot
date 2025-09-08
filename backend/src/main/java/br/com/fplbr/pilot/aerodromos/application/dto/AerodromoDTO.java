package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.fplbr.pilot.aerodromos.application.dto.CartaAerodromoDTO;

/**
 * DTO para representaÃƒÂ§ÃƒÂ£o de um aerÃƒÂ³dromo na API.
 * Inclui informaÃƒÂ§ÃƒÂµes bÃƒÂ¡sicas, pistas, cartas e informaÃƒÂ§ÃƒÂµes de sol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AerodromoDTO {
    
    /**
     * Converte um objeto de domÃƒÂ­nio Aerodromo para DTO.
     * 
     * @param aerodromo O objeto de domÃƒÂ­nio a ser convertido
     * @return O DTO correspondente, ou null se o parÃƒÂ¢metro for null
     */
    public static AerodromoDTO fromDomain(Aerodromo aerodromo) {
        if (aerodromo == null) {
            return null;
        }
        
        return AerodromoDTO.builder()
                .icao(aerodromo.getIcao())
                .iata(aerodromo.getIata())
                .nomeOficial(aerodromo.getNome())
                .municipio(aerodromo.getMunicipio())
                .uf(aerodromo.getUf())
                .regiao(aerodromo.getRegiao())
                .latitude(aerodromo.getLatitude() != null ? aerodromo.getLatitude().toString() : null)
                .longitude(aerodromo.getLongitude() != null ? aerodromo.getLongitude().toString() : null)
                .altitudePistaPes(aerodromo.getAltitudePes() != null ? aerodromo.getAltitudePes().toString() : null)
                .tipo(aerodromo.getTipo())
                .uso(aerodromo.getUso())
                .cindacta(aerodromo.getCindacta())
                .internacional(aerodromo.isInternacional())
                .terminal(aerodromo.isTerminal())
                .horarioFuncionamento(aerodromo.getHorarioFuncionamento())
                .telefone(aerodromo.getTelefone())
                .email(aerodromo.getEmail())
                .responsavel(aerodromo.getResponsavel())
                .observacoes(aerodromo.getObservacoes())
                .ativo(aerodromo.isAtivo())
                .build();
    }
    
    /**
     * Converte este DTO para um objeto de domÃƒÂ­nio Aerodromo.
     * 
     * @return O objeto de domÃƒÂ­nio correspondente
     */
    public Aerodromo toDomain() {
        return Aerodromo.builder()
                .icao(this.icao)
                .iata(this.iata)
                .nome(this.nomeOficial)
                .municipio(this.municipio)
                .uf(this.uf)
                .regiao(this.regiao)
                .latitude(this.latitude != null ? Double.parseDouble(this.latitude) : null)
                .longitude(this.longitude != null ? Double.parseDouble(this.longitude) : null)
                .altitudePes(this.altitudePistaPes != null ? Integer.parseInt(this.altitudePistaPes) : null)
                .tipo(this.tipo)
                .uso(this.uso)
                .cindacta(this.cindacta)
                .internacional(this.internacional != null && this.internacional)
                .terminal(this.terminal != null && this.terminal)
                .horarioFuncionamento(this.horarioFuncionamento)
                .telefone(this.telefone)
                .email(this.email)
                .responsavel(this.responsavel)
                .observacoes(this.observacoes)
                .ativo(this.ativo != null && this.ativo)
                .build();
    }
    // IdentificaÃƒÂ§ÃƒÂ£o
    private String icao;                   // CÃƒÂ³digo ICAO do aerÃƒÂ³dromo (4 letras)
    private String iata;                   // CÃƒÂ³digo IATA (opcional, 3 letras)
    private String nomeOficial;            // Nome oficial do aerÃƒÂ³dromo
    
    // LocalizaÃƒÂ§ÃƒÂ£o
    private String municipio;              // MunicÃƒÂ­pio onde estÃƒÂ¡ localizado
    private String uf;                     // Unidade Federativa (estado)
    private String regiao;                 // RegiÃƒÂ£o do Brasil
    private String latitude;               // Latitude em graus decimais (string para evitar problemas de precisÃƒÂ£o)
    private String longitude;              // Longitude em graus decimais (string para evitar problemas de precisÃƒÂ£o)
    private String altitudePistaPes;       // Altitude da pista em pÃƒÂ©s
    
    // InformaÃƒÂ§ÃƒÂµes gerais
    private String tipo;                   // Tipo de aerÃƒÂ³dromo (AD, HELIPONTO, etc)
    private String uso;                    // Uso (PÃƒÂºblico, Privado, Misto)
    private String cindacta;               // CÃƒÂ³digo CINDACTA (se aplicÃƒÂ¡vel)
    private Boolean internacional;         // Se ÃƒÂ© um aeroporto internacional
    private Boolean terminal;              // Se ÃƒÂ© um aeroporto terminal
    private String horarioFuncionamento;   // HorÃƒÂ¡rio de funcionamento
    private String telefone;               // Telefone para contato
    private String email;                  // E-mail para contato
    private String responsavel;            // ResponsÃƒÂ¡vel pelo aerÃƒÂ³dromo
    private String observacoes;            // ObservaÃƒÂ§ÃƒÂµes adicionais
    private Boolean ativo;                 // Se o aerÃƒÂ³dromo estÃƒÂ¡ ativo
    
    // InformaÃƒÂ§ÃƒÂµes adicionais
    @Builder.Default
    private List<CartaAerodromoDTO> cartas = new ArrayList<>();  // Cartas aeronÃƒÂ¡uticas disponÃƒÂ­veis
    private String nascerSol;              // HorÃƒÂ¡rio do nascer do sol
    private String porDoSol;               // HorÃƒÂ¡rio do pÃƒÂ´r do sol
    
    // Additional setter methods for the fields
    public void setCartas(List<CartaAerodromoDTO> cartas) {
        this.cartas = cartas != null ? cartas : new ArrayList<>();
    }
    
    public void setNascerSol(String nascerSol) {
        this.nascerSol = nascerSol;
    }
    
    public void setPorDoSol(String porDoSol) {
        this.porDoSol = porDoSol;
    }
}
