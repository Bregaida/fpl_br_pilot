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

    // Campo 7 - IdentificaÃ§Ã£o da Aeronave
    @NotBlank(message = "IdentificaÃ§Ã£o da aeronave Ã© obrigatÃ³ria")
    @Size(max = 7, message = "IdentificaÃ§Ã£o da aeronave deve ter no mÃ¡ximo 7 caracteres")
    private String identificacaoDaAeronave;

    @Size(max = 20, message = "Indicativo de chamada deve ter no mÃ¡ximo 20 caracteres")
    private String indicativoDeChamada;

    // Campo 8 - Regras de Voo e Tipo de Voo
    @NotNull(message = "Regra de voo Ã© obrigatÃ³ria")
    private RegraDeVooEnum regraDeVooEnum;

    @NotNull(message = "Tipo de voo Ã© obrigatÃ³rio")
    private TipoDeVooEnum tipoDeVooEnum;

    // Campo 9 - InformaÃ§Ãµes da Aeronave
    @Min(value = 1, message = "NÃºmero de aeronaves deve ser no mÃ­nimo 1")
    private Integer numeroDeAeronaves;

    @NotBlank(message = "Tipo de aeronave Ã© obrigatÃ³rio")
    @Size(max = 4, message = "Tipo de aeronave deve ter no mÃ¡ximo 4 caracteres")
    private String tipoDeAeronave;

    @NotNull(message = "Categoria de esteira de turbulÃªncia Ã© obrigatÃ³ria")
    private CategoriaEsteiraTurbulenciaEnum categoriaEsteiraTurbulenciaEnum;

    // Campo 10 - Equipamentos e VigilÃ¢ncia
    @NotNull(message = "Equipamento e capacidade da aeronave sÃ£o obrigatÃ³rios")
    @Valid
    private EquipamentoCapacidadeDaAeronave equipamentoCapacidadeDaAeronave;

    @NotNull(message = "VigilÃ¢ncia Ã© obrigatÃ³ria")
    @Valid
    private Vigilancia vigilancia;

    // Campo 13 - InformaÃ§Ãµes de Partida
    @NotBlank(message = "AerÃ³dromo de partida Ã© obrigatÃ³rio")
    @Size(min = 4, max = 4, message = "AerÃ³dromo de partida deve ter 4 caracteres")
    private String aerodromoDePartida;

    @NotNull(message = "Hora de partida Ã© obrigatÃ³ria")
    @FutureOrPresent(message = "Hora de partida deve ser uma data futura ou presente")
    private LocalDateTime horaPartida;

    // Campo 16 - Destino e Alternativas
    @NotBlank(message = "AerÃ³dromo de destino Ã© obrigatÃ³rio")
    @Size(min = 4, max = 4, message = "AerÃ³dromo de destino deve ter 4 caracteres")
    private String aerodromoDeDestino;

    @NotNull(message = "Tempo de voo previsto Ã© obrigatÃ³rio")
    private LocalDateTime tempoDeVooPrevisto;

    @NotBlank(message = "AerÃ³dromo de alternativa Ã© obrigatÃ³rio")
    @Size(min = 4, max = 4, message = "AerÃ³dromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativa;

    @Size(min = 4, max = 4, message = "Segundo aerÃ³dromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativaSegundo;

    // Campo 15 - InformaÃ§Ãµes de Rota
    @NotBlank(message = "Velocidade de cruzeiro Ã© obrigatÃ³ria")
    private String velocidadeDeCruzeiro;

    @NotBlank(message = "NÃ­vel de voo Ã© obrigatÃ³rio")
    @Pattern(regexp = "^[Ff]?\\d{3}$", message = "Formato de nÃ­vel de voo invÃ¡lido. Use F seguido de 3 dÃ­gitos")
    private String nivelDeVoo;

    @NotBlank(message = "Rota Ã© obrigatÃ³ria")
    private String rota;

    // Campo 18 - Outras InformaÃ§Ãµes
    @NotNull(message = "Outras informaÃ§Ãµes sÃ£o obrigatÃ³rias")
    @Valid
    private OutrasInformacoes outrasInformacoes;

    @NotNull(message = "Data de operaÃ§Ã£o do voo Ã© obrigatÃ³ria")
    private LocalDate dof = LocalDate.now();

    // Campo 19 - InformaÃ§Ãµes Suplementares
    @NotNull(message = "InformaÃ§Ãµes suplementares sÃ£o obrigatÃ³rias")
    @Valid
    private InformacaoSuplementar informacaoSuplementar;

    // Campos de auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
