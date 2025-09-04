package br.com.fplbr.pilot.dto;

import br.com.fplbr.pilot.model.*;
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

    // Campo 7 - Identificação da Aeronave
    @NotBlank(message = "Identificação da aeronave é obrigatória")
    @Size(max = 7, message = "Identificação da aeronave deve ter no máximo 7 caracteres")
    private String identificacaoDaAeronave;

    @Size(max = 20, message = "Indicativo de chamada deve ter no máximo 20 caracteres")
    private String indicativoDeChamada;

    // Campo 8 - Regras de Voo e Tipo de Voo
    @NotNull(message = "Regra de voo é obrigatória")
    private RegraDeVooEnum regraDeVooEnum;

    @NotNull(message = "Tipo de voo é obrigatório")
    private TipoDeVooEnum tipoDeVooEnum;

    // Campo 9 - Informações da Aeronave
    @Min(value = 1, message = "Número de aeronaves deve ser no mínimo 1")
    private Integer numeroDeAeronaves;

    @NotBlank(message = "Tipo de aeronave é obrigatório")
    @Size(max = 4, message = "Tipo de aeronave deve ter no máximo 4 caracteres")
    private String tipoDeAeronave;

    @NotNull(message = "Categoria de esteira de turbulência é obrigatória")
    private CategoriaEsteiraTurbulenciaEnum categoriaEsteiraTurbulenciaEnum;

    // Campo 10 - Equipamentos e Vigilância
    @NotNull(message = "Equipamento e capacidade da aeronave são obrigatórios")
    @Valid
    private EquipamentoCapacidadeDaAeronave equipamentoCapacidadeDaAeronave;

    @NotNull(message = "Vigilância é obrigatória")
    @Valid
    private Vigilancia vigilancia;

    // Campo 13 - Informações de Partida
    @NotBlank(message = "Aeródromo de partida é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de partida deve ter 4 caracteres")
    private String aerodromoDePartida;

    @NotNull(message = "Hora de partida é obrigatória")
    @FutureOrPresent(message = "Hora de partida deve ser uma data futura ou presente")
    private LocalDateTime horaPartida;

    // Campo 16 - Destino e Alternativas
    @NotBlank(message = "Aeródromo de destino é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de destino deve ter 4 caracteres")
    private String aerodromoDeDestino;

    @NotNull(message = "Tempo de voo previsto é obrigatório")
    private LocalDateTime tempoDeVooPrevisto;

    @NotBlank(message = "Aeródromo de alternativa é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativa;

    @Size(min = 4, max = 4, message = "Segundo aeródromo de alternativa deve ter 4 caracteres")
    private String aerodromoDeAlternativaSegundo;

    // Campo 15 - Informações de Rota
    @NotBlank(message = "Velocidade de cruzeiro é obrigatória")
    private String velocidadeDeCruzeiro;

    @NotBlank(message = "Nível de voo é obrigatório")
    @Pattern(regexp = "^[Ff]?\\d{3}$", message = "Formato de nível de voo inválido. Use F seguido de 3 dígitos")
    private String nivelDeVoo;

    @NotBlank(message = "Rota é obrigatória")
    private String rota;

    // Campo 18 - Outras Informações
    @NotNull(message = "Outras informações são obrigatórias")
    @Valid
    private OutrasInformacoes outrasInformacoes;

    @NotNull(message = "Data de operação do voo é obrigatória")
    private LocalDate dof = LocalDate.now();

    // Campo 19 - Informações Suplementares
    @NotNull(message = "Informações suplementares são obrigatórias")
    @Valid
    private InformacaoSuplementar informacaoSuplementar;

    // Campos de auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
