package br.com.fplbr.pilot.flightplan.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import br.com.fplbr.pilot.flightplan.application.validator.ValidFlightPlan;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "flight_plans")
@ValidFlightPlan
public class FlightPlan extends PanacheEntityBase {
    // Getters e Setters manuais
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentificacaoDaAeronave() { return identificacaoDaAeronave; }
    public void setIdentificacaoDaAeronave(String identificacaoDaAeronave) { this.identificacaoDaAeronave = identificacaoDaAeronave; }
    public String getIndicativoDeChamada() { return indicativoDeChamada; }
    public void setIndicativoDeChamada(String indicativoDeChamada) { this.indicativoDeChamada = indicativoDeChamada; }
    public RegraDeVooEnum getRegraDeVooEnum() { return regraDeVooEnum; }
    public void setRegraDeVooEnum(RegraDeVooEnum regraDeVooEnum) { this.regraDeVooEnum = regraDeVooEnum; }
    public TipoDeVooEnum getTipoDeVooEnum() { return tipoDeVooEnum; }
    public void setTipoDeVooEnum(TipoDeVooEnum tipoDeVooEnum) { this.tipoDeVooEnum = tipoDeVooEnum; }
    public Integer getNumeroDeAeronaves() { return numeroDeAeronaves; }
    public void setNumeroDeAeronaves(Integer numeroDeAeronaves) { this.numeroDeAeronaves = numeroDeAeronaves; }
    public String getTipoDeAeronave() { return tipoDeAeronave; }
    public void setTipoDeAeronave(String tipoDeAeronave) { this.tipoDeAeronave = tipoDeAeronave; }
    public CategoriaEsteiraTurbulenciaEnum getCategoriaEsteiraTurbulenciaEnum() { return categoriaEsteiraTurbulenciaEnum; }
    public void setCategoriaEsteiraTurbulenciaEnum(CategoriaEsteiraTurbulenciaEnum categoriaEsteiraTurbulenciaEnum) { this.categoriaEsteiraTurbulenciaEnum = categoriaEsteiraTurbulenciaEnum; }
    public EquipamentoCapacidadeDaAeronave getEquipamentoCapacidadeDaAeronave() { return equipamentoCapacidadeDaAeronave; }
    public void setEquipamentoCapacidadeDaAeronave(EquipamentoCapacidadeDaAeronave equipamentoCapacidadeDaAeronave) { this.equipamentoCapacidadeDaAeronave = equipamentoCapacidadeDaAeronave; }
    public Vigilancia getVigilancia() { return vigilancia; }
    public void setVigilancia(Vigilancia vigilancia) { this.vigilancia = vigilancia; }
    public String getAerodromoDePartida() { return aerodromoDePartida; }
    public void setAerodromoDePartida(String aerodromoDePartida) { this.aerodromoDePartida = aerodromoDePartida; }
    public LocalDateTime getHoraPartida() { return horaPartida; }
    public void setHoraPartida(LocalDateTime horaPartida) { this.horaPartida = horaPartida; }
    public String getAerodromoDeDestino() { return aerodromoDeDestino; }
    public void setAerodromoDeDestino(String aerodromoDeDestino) { this.aerodromoDeDestino = aerodromoDeDestino; }
    public LocalDateTime getTempoDeVooPrevisto() { return tempoDeVooPrevisto; }
    public void setTempoDeVooPrevisto(LocalDateTime tempoDeVooPrevisto) { this.tempoDeVooPrevisto = tempoDeVooPrevisto; }
    public String getAerodromoDeAlternativa() { return aerodromoDeAlternativa; }
    public void setAerodromoDeAlternativa(String aerodromoDeAlternativa) { this.aerodromoDeAlternativa = aerodromoDeAlternativa; }
    public String getAerodromoDeAlternativaSegundo() { return aerodromoDeAlternativaSegundo; }
    public void setAerodromoDeAlternativaSegundo(String aerodromoDeAlternativaSegundo) { this.aerodromoDeAlternativaSegundo = aerodromoDeAlternativaSegundo; }
    public String getVelocidadeDeCruzeiro() { return velocidadeDeCruzeiro; }
    public void setVelocidadeDeCruzeiro(String velocidadeDeCruzeiro) { this.velocidadeDeCruzeiro = velocidadeDeCruzeiro; }
    public String getNivelDeVoo() { return nivelDeVoo; }
    public void setNivelDeVoo(String nivelDeVoo) { this.nivelDeVoo = nivelDeVoo; }
    public String getRota() { return rota; }
    public void setRota(String rota) { this.rota = rota; }
    public OutrasInformacoes getOutrasInformacoes() { return outrasInformacoes; }
    public void setOutrasInformacoes(OutrasInformacoes outrasInformacoes) { this.outrasInformacoes = outrasInformacoes; }
    public LocalDate getDof() { return dof; }
    public void setDof(LocalDate dof) { this.dof = dof; }
    public InformacaoSuplementar getInformacaoSuplementar() { return informacaoSuplementar; }
    public void setInformacaoSuplementar(InformacaoSuplementar informacaoSuplementar) { this.informacaoSuplementar = informacaoSuplementar; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo 7 - Identificação da Aeronave
    @NotBlank(message = "Identificação da aeronave é obrigatória")
    @Size(max = 5, message = "Identificação da aeronave deve ter no máximo 5 caracteres")
    @Column(name = "identificacao_aeronave", nullable = false, length = 5)
    private String identificacaoDaAeronave;

    @Column(name = "indicativo_chamada", length = 20)
    private String indicativoDeChamada;

    // Campo 8 - Regras de Voo e Tipo de Voo
    @NotNull(message = "Regra de voo é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "regra_voo", nullable = false, length = 10)
    private RegraDeVooEnum regraDeVooEnum;

    @NotNull(message = "Tipo de voo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_voo", nullable = false, length = 30)
    private TipoDeVooEnum tipoDeVooEnum;

    // Campo 9 - Informações da Aeronave
    @Min(value = 1, message = "NÃƒÂºmero de aeronaves deve ser no mínimo 1")
    @Column(name = "numero_aeronaves")
    private Integer numeroDeAeronaves;

    @NotBlank(message = "Tipo de aeronave é obrigatório")
    @Size(max = 4, message = "Tipo de aeronave deve ter no máximo 4 caracteres")
    @Column(name = "tipo_aeronave", nullable = false, length = 4)
    private String tipoDeAeronave;

    @NotNull(message = "Categoria de esteira de turbulência é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_esteira", nullable = false, length = 10)
    private CategoriaEsteiraTurbulenciaEnum categoriaEsteiraTurbulenciaEnum;

    // Campo 10 - Equipamentos e VigilÃƒÂ¢ncia (nÃƒÂ£o persistir campos detalhados para evitar conflito de colunas)
    @Transient
    private EquipamentoCapacidadeDaAeronave equipamentoCapacidadeDaAeronave;

    @Transient
    private Vigilancia vigilancia;

    // Campo 13 - Informações de Partida
    @NotBlank(message = "Aeródromo de partida é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de partida deve ter 4 caracteres")
    @Column(name = "aerodromo_partida", nullable = false, length = 4)
    private String aerodromoDePartida;

    @NotNull(message = "Hora de partida é obrigatória")
    @FutureOrPresent(message = "Hora de partida deve ter no mínimo 30 minutos da hora atual")
    @Column(name = "hora_partida", nullable = false)
    private LocalDateTime horaPartida;

    // Campo 16 - Destino e Alternativas
    @NotBlank(message = "Aeródromo de destino é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de destino deve ter 4 caracteres")
    @Column(name = "aerodromo_destino", nullable = false, length = 4)
    private String aerodromoDeDestino;

    @NotNull(message = "Tempo de voo previsto é obrigatório")
    @Column(name = "tempo_voo_previsto", nullable = false)
    private LocalDateTime tempoDeVooPrevisto;

    @NotBlank(message = "Aeródromo de alternativa é obrigatório")
    @Size(min = 4, max = 4, message = "Aeródromo de alternativa deve ter 4 caracteres")
    @Column(name = "aerodromo_alternativa", nullable = false, length = 4)
    private String aerodromoDeAlternativa;

    @Size(min = 4, max = 4, message = "Segundo aeródromo de alternativa deve ter 4 caracteres")
    @Column(name = "aerodromo_alternativa_segundo", length = 4)
    private String aerodromoDeAlternativaSegundo;

    // Campo 15 - Informações de Rota
    @NotBlank(message = "Velocidade de cruzeiro é obrigatória")
    @Column(name = "velocidade_cruzeiro", nullable = false, length = 10)
    private String velocidadeDeCruzeiro;

    @NotBlank(message = "Nível de voo é obrigatório")
    @Pattern(regexp = "^[Ff]?\\d{3}$", message = "Formato de nível de voo inválido. Use F seguido de 3 dígitos")
    @Column(name = "nivel_voo", nullable = false, length = 4)
    private String nivelDeVoo;

    @NotBlank(message = "Rota é obrigatória")
    @Column(name = "rota", nullable = false, columnDefinition = "TEXT")
    private String rota;

    // Campo 18 - Outras Informações
    @Transient
    private OutrasInformacoes outrasInformacoes;

    @NotNull(message = "Data de operação do voo é obrigatória")
    @Column(name = "dof", nullable = false)
    private LocalDate dof = LocalDate.now();

    // Campo 19 - Informações Suplementares
    @Transient
    private InformacaoSuplementar informacaoSuplementar;

    // Campos de auditoria
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
