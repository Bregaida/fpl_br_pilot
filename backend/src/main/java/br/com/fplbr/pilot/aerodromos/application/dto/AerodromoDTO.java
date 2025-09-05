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
 * DTO para representação de um aeródromo na API.
 * Inclui informações básicas, pistas, cartas e informações de sol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AerodromoDTO {
    
    /**
     * Converte um objeto de domínio Aerodromo para DTO.
     * 
     * @param aerodromo O objeto de domínio a ser convertido
     * @return O DTO correspondente, ou null se o parâmetro for null
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
     * Converte este DTO para um objeto de domínio Aerodromo.
     * 
     * @return O objeto de domínio correspondente
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
    // Identificação
    private String icao;                   // Código ICAO do aeródromo (4 letras)
    private String iata;                   // Código IATA (opcional, 3 letras)
    private String nomeOficial;            // Nome oficial do aeródromo
    
    // Localização
    private String municipio;              // Município onde está localizado
    private String uf;                     // Unidade Federativa (estado)
    private String regiao;                 // Região do Brasil
    private String latitude;               // Latitude em graus decimais (string para evitar problemas de precisão)
    private String longitude;              // Longitude em graus decimais (string para evitar problemas de precisão)
    private String altitudePistaPes;       // Altitude da pista em pés
    
    // Informações gerais
    private String tipo;                   // Tipo de aeródromo (AD, HELIPONTO, etc)
    private String uso;                    // Uso (Público, Privado, Misto)
    private String cindacta;               // Código CINDACTA (se aplicável)
    private Boolean internacional;         // Se é um aeroporto internacional
    private Boolean terminal;              // Se é um aeroporto terminal
    private String horarioFuncionamento;   // Horário de funcionamento
    private String telefone;               // Telefone para contato
    private String email;                  // E-mail para contato
    private String responsavel;            // Responsável pelo aeródromo
    private String observacoes;            // Observações adicionais
    private Boolean ativo;                 // Se o aeródromo está ativo
    
    // Informações adicionais
    @Builder.Default
    private List<CartaAerodromoDTO> cartas = new ArrayList<>();  // Cartas aeronáuticas disponíveis
    private String nascerSol;              // Horário do nascer do sol
    private String porDoSol;               // Horário do pôr do sol
    
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
