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
 * DTO para representaÃ§Ã£o de um aerÃ³dromo na API.
 * Inclui informaÃ§Ãµes bÃ¡sicas, pistas, cartas e informaÃ§Ãµes de sol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AerodromoDTO {
    
    /**
     * Converte um objeto de domÃ­nio Aerodromo para DTO.
     * 
     * @param aerodromo O objeto de domÃ­nio a ser convertido
     * @return O DTO correspondente, ou null se o parÃ¢metro for null
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
     * Converte este DTO para um objeto de domÃ­nio Aerodromo.
     * 
     * @return O objeto de domÃ­nio correspondente
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
    // IdentificaÃ§Ã£o
    private String icao;                   // CÃ³digo ICAO do aerÃ³dromo (4 letras)
    private String iata;                   // CÃ³digo IATA (opcional, 3 letras)
    private String nomeOficial;            // Nome oficial do aerÃ³dromo
    
    // LocalizaÃ§Ã£o
    private String municipio;              // MunicÃ­pio onde estÃ¡ localizado
    private String uf;                     // Unidade Federativa (estado)
    private String regiao;                 // RegiÃ£o do Brasil
    private String latitude;               // Latitude em graus decimais (string para evitar problemas de precisÃ£o)
    private String longitude;              // Longitude em graus decimais (string para evitar problemas de precisÃ£o)
    private String altitudePistaPes;       // Altitude da pista em pÃ©s
    
    // InformaÃ§Ãµes gerais
    private String tipo;                   // Tipo de aerÃ³dromo (AD, HELIPONTO, etc)
    private String uso;                    // Uso (PÃºblico, Privado, Misto)
    private String cindacta;               // CÃ³digo CINDACTA (se aplicÃ¡vel)
    private Boolean internacional;         // Se Ã© um aeroporto internacional
    private Boolean terminal;              // Se Ã© um aeroporto terminal
    private String horarioFuncionamento;   // HorÃ¡rio de funcionamento
    private String telefone;               // Telefone para contato
    private String email;                  // E-mail para contato
    private String responsavel;            // ResponsÃ¡vel pelo aerÃ³dromo
    private String observacoes;            // ObservaÃ§Ãµes adicionais
    private Boolean ativo;                 // Se o aerÃ³dromo estÃ¡ ativo
    
    // InformaÃ§Ãµes adicionais
    @Builder.Default
    private List<CartaAerodromoDTO> cartas = new ArrayList<>();  // Cartas aeronÃ¡uticas disponÃ­veis
    private String nascerSol;              // HorÃ¡rio do nascer do sol
    private String porDoSol;               // HorÃ¡rio do pÃ´r do sol
    
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
