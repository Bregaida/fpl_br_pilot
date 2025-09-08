package br.com.fplbr.pilot.flightplan.infrastructure.web.dto;

import br.com.fplbr.pilot.flightplan.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightPlanDTO {

    private Long id;

    // Campo 7 - IdentificaÃƒÂ§ÃƒÂ£o da Aeronave
    @NotBlank(message = "IdentificaÃƒÂ§ÃƒÂ£o da aeronave ÃƒÂ© obrigatÃƒÂ³ria")
    @Size(max = 7, message = "IdentificaÃƒÂ§ÃƒÂ£o da aeronave deve ter no mÃƒÂ¡ximo 7 caracteres")
    private String identificacaoDaAeronave;

    @Size(max = 20, message = "Indicativo de chamada deve ter no mÃƒÂ¡ximo 20 caracteres")
    private String indicativoDeChamada;

    // Campo 8 - Regras de Voo e Tipo de Voo
    @NotNull(message = "Regra de voo ÃƒÂ© obrigatÃƒÂ³ria")
    private RegraDeVooEnum regraDeVooEnum;

    @NotNull(message = "Tipo de voo ÃƒÂ© obrigatÃƒÂ³rio")
    private TipoDeVooEnum tipoDeVooEnum;

    // Campo 9 - InformaÃƒÂ§ÃƒÂµes da Aeronave
    @Min(value = 1, message = "NÃƒÂºmero de aeronaves deve ser no mÃƒÂ­nimo 1")
    private Integer numeroDeAeronaves;

    @NotBlank(message = "Tipo de aeronave ÃƒÂ© obrigatÃƒÂ³rio")
    @Size(max = 4, message = "Tipo de aeronave deve ter no mÃƒÂ¡ximo 4 caracteres")
    private String tipoDeAeronave;

    @NotNull(message = "Categoria de esteira de turbulÃƒÂªncia ÃƒÂ© obrigatÃƒÂ³ria")
    private CategoriaEsteiraTurbulenciaEnum categoriaEsteiraTurbulenciaEnum;

    // Campo 10 - Equipamentos e VigilÃƒÂ¢ncia
    @NotNull(message = "Equipamento e capacidade da aeronave sÃƒÂ£o obrigatÃƒÂ³rios")
    @Valid
    private EquipamentoCapacidadeDaAeronave equipamentoCapacidadeDaAeronave;

    @NotNull(message = "VigilÃƒÂ¢ncia ÃƒÂ© obrigatÃƒÂ³ria")
    @Valid
    private Vigilancia vigilancia;

    // Campo 13 - InformaÃƒÂ§ÃƒÂµes de Partida
    @NotBlank(message = "AerÃƒÂ³dromo de partida ÃƒÂ© obrigatÃƒÂ³rio")
    @Size(min = 4, max = 4, message = "AerÃƒÂ³dromo de partida deve ter 4 caracteres")
    private String aerodromoDePartida;

    @NotNull(message = "Hora de partida ÃƒÂ© obrigatÃƒÂ³ria")
    @FutureOrPresent(message = "Hora de partida deve ser uma data futura ou presente")
    private LocalDateTime horaPartida;

    // Campo 16 - Destino e Alternativas
    @NotBlank(message = "AerÃƒÂ³dromo de destino ÃƒÂ© obrigatÃƒÂ³rio")
    @Size(min = 4, max = 4, message = "AerÃƒÂ³dromo de destino deve ter 4 caracteres")
    private String aerodromoDeDestino;

    @NotNull(message = "Tempo de voo previsto ÃƒÂ© obrigatÃƒÂ³rio")
    private LocalDateTime tempoDeVooPrevisto;

    @NotBlank(message = "AerÃƒÂ³dromo de alternativa ÃƒÂ© obrigatÃƒÂ³rio")
    @Size(min = 4, max = 4, message = "AerÃƒÂ³dromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativa;

    @Size(min = 4, max = 4, message = "Segundo aerÃƒÂ³dromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativaSegundo;

    // Campo 15 - InformaÃƒÂ§ÃƒÂµes de Rota
    @NotBlank(message = "Velocidade de cruzeiro ÃƒÂ© obrigatÃƒÂ³ria")
    private String velocidadeDeCruzeiro;

    @NotBlank(message = "NÃƒÂ­vel de voo ÃƒÂ© obrigatÃƒÂ³rio")
    @Pattern(regexp = "^[Ff]?\\d{3}$", message = "Formato de nÃƒÂ­vel de voo invÃƒÂ¡lido. Use F seguido de 3 dÃƒÂ­gitos")
    private String nivelDeVoo;

    @NotBlank(message = "Rota ÃƒÂ© obrigatÃƒÂ³ria")
    private String rota;

    // Campo 18 - Outras InformaÃƒÂ§ÃƒÂµes
    @NotNull(message = "Outras informaÃƒÂ§ÃƒÂµes sÃƒÂ£o obrigatÃƒÂ³rias")
    @Valid
    private OutrasInformacoes outrasInformacoes;

    @NotNull(message = "Data de operaÃƒÂ§ÃƒÂ£o do voo ÃƒÂ© obrigatÃƒÂ³ria")
    private LocalDate dof = LocalDate.now();

    // Campo 19 - InformaÃƒÂ§ÃƒÂµes Suplementares
    @NotNull(message = "InformaÃƒÂ§ÃƒÂµes suplementares sÃƒÂ£o obrigatÃƒÂ³rias")
    @Valid
    private InformacaoSuplementar informacaoSuplementar;

    // Campos de auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
