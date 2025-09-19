package br.com.fplbr.pilot.flightplan.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.List;

public class PlanoDeVooDTO {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{3,7}$")
    private String identificacaoDaAeronave;

    @NotNull
    private Boolean indicativoDeChamada;

    @NotBlank
    @Pattern(regexp = "^[IVYZ]$")
    private String regraDeVooEnum;

    @NotBlank
    @Pattern(regexp = "^[GSNMX]$")
    private String tipoDeVooEnum;

    @NotNull
    @Min(1)
    private Integer numeroDeAeronaves;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{2,4}$")
    private String tipoDeAeronave;

    @NotBlank
    @Pattern(regexp = "^[LMHJ]$")
    private String categoriaEsteiraTurbulenciaEnum;

    @NotNull
    @Size(min = 1)
    private List<@NotBlank String> equipamentoCapacidadeDaAeronave;

    @NotNull
    @Size(min = 1)
    private List<@NotBlank String> vigilancia;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDePartida;

    @NotNull
    private OffsetDateTime horaPartida; // ISO UTC esperado

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDeDestino;

    @NotBlank
    @Pattern(regexp = "^([0-1]\\d|2[0-3])[0-5]\\d$")
    private String tempoDeVooPrevisto; // HHmm

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDeAlternativa;

    @Pattern(regexp = "^$|^[A-Z]{4}$")
    private String aerodromoDeAlternativaSegundo; // opcional

    @NotBlank
    @Pattern(regexp = "^[NKM][0-9]{4}$")
    private String velocidadeDeCruzeiro;

    @NotBlank
    @Pattern(regexp = "^(VFR|[AF][0-9]{3})$")
    private String nivelDeVoo;

    @NotBlank
    private String rota;

    @Valid
    @NotNull
    private OutrasInformacoes outrasInformacoes;

    @Valid
    @NotNull
    private InformacaoSuplementar informacaoSuplementar;

    @NotBlank
    @Pattern(regexp = "^(PVC|PVS)$")
    private String modo; // PVC | PVS
    
    // Campo adicional para compatibilidade
    private Object infoVoo;

    // ==== Tipos aninhados ====

    public static class OutrasInformacoes {
        @Pattern(regexp = "^$|^(?:[A-Z]{3,4}[0-9]{4})(?:\\s+[A-Z]{3,4}[0-9]{4})*$")
        private String eet; // SBC0005 SBBS0150 ...

        @NotBlank
        private String opr;

        @NotBlank
        @Pattern(regexp = "^[A-Z]{4}$")
        private String from;

        private List<@Pattern(regexp = "^[A-Z]$") String> per; // PVC

        private String rmk; // deve conter REA quando rota tiver REA

        @NotBlank
        @Pattern(regexp = "^\\d{6}$")
        private String dof; // ddmmaa
        
        // Construtor padrão
        public OutrasInformacoes() {}
        
        // Getters e Setters
        public String getEet() { return eet; }
        public void setEet(String eet) { this.eet = eet; }
        
        public String getOpr() { return opr; }
        public void setOpr(String opr) { this.opr = opr; }
        
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        
        public List<String> getPer() { return per; }
        public void setPer(List<String> per) { this.per = per; }
        
        public String getRmk() { return rmk; }
        public void setRmk(String rmk) { this.rmk = rmk; }
        
        public String getDof() { return dof; }
        public void setDof(String dof) { this.dof = dof; }
    }

    public static class BotesInfo {
        @NotNull
        private Boolean possui;
        @Min(1)
        private Integer numero;
        @Min(1)
        private Integer capacidade;
        private Boolean c; // abrigo
        private String cor;
        
        // Construtor padrão
        public BotesInfo() {}
        
        // Getters e Setters
        public Boolean getPossui() { return possui; }
        public void setPossui(Boolean possui) { this.possui = possui; }
        
        public Integer getNumero() { return numero; }
        public void setNumero(Integer numero) { this.numero = numero; }
        
        public Integer getCapacidade() { return capacidade; }
        public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
        
        public Boolean getC() { return c; }
        public void setC(Boolean c) { this.c = c; }
        
        public String getCor() { return cor; }
        public void setCor(String cor) { this.cor = cor; }
    }

    public static class InformacaoSuplementar {
        @NotBlank
        @Pattern(regexp = "^([0-1]\\d|2[0-3])[0-5]\\d$")
        private String autonomia; // HHmm

        @NotNull
        @Min(1)
        private Integer pob; // pessoas a bordo

        private List<@Pattern(regexp = "^[UVE]$") String> radioEmergencia;
        private List<@Pattern(regexp = "^[SPDMJ]$") String> sobrevivencia;
        private List<@Pattern(regexp = "^[JLFUV]$") String> coletes;

        @Valid
        private BotesInfo botes;

        @NotBlank
        private String corEMarcaAeronave;

        private String observacoes;

        @NotBlank
        private String pilotoEmComando;

        @NotBlank
        private String anacPrimeiroPiloto;

        private String anacSegundoPiloto;

        @NotBlank
        @Pattern(regexp = "^\\d{10,11}$")
        private String telefone;

        private Boolean n; // se true, observações obrigatório
        
        // Métodos básicos para compilar
        public InformacaoSuplementar() {}
        
        public InformacaoSuplementar(String anacPrimeiroPiloto, String anacSegundoPiloto, String telefone, Boolean n) {
            this.anacPrimeiroPiloto = anacPrimeiroPiloto;
            this.anacSegundoPiloto = anacSegundoPiloto;
            this.telefone = telefone;
            this.n = n;
        }
        
        public String getAnacPrimeiroPiloto() { return anacPrimeiroPiloto; }
        public void setAnacPrimeiroPiloto(String anacPrimeiroPiloto) { this.anacPrimeiroPiloto = anacPrimeiroPiloto; }
        public String getAnacSegundoPiloto() { return anacSegundoPiloto; }
        public void setAnacSegundoPiloto(String anacSegundoPiloto) { this.anacSegundoPiloto = anacSegundoPiloto; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public Boolean getN() { return n; }
        public void setN(Boolean n) { this.n = n; }
        
        // Métodos faltantes para compilar
        public String getAutonomia() { return autonomia; }
        public void setAutonomia(String autonomia) { this.autonomia = autonomia; }
        
        public Integer getPob() { return pob; }
        public void setPob(Integer pob) { this.pob = pob; }
        
        public List<String> getRadioEmergencia() { return radioEmergencia; }
        public void setRadioEmergencia(List<String> radioEmergencia) { this.radioEmergencia = radioEmergencia; }
        
        public List<String> getSobrevivencia() { return sobrevivencia; }
        public void setSobrevivencia(List<String> sobrevivencia) { this.sobrevivencia = sobrevivencia; }
        
        public List<String> getColetes() { return coletes; }
        public void setColetes(List<String> coletes) { this.coletes = coletes; }
        
        public BotesInfo getBotes() { return botes; }
        public void setBotes(BotesInfo botes) { this.botes = botes; }
        
        public String getCorEMarcaAeronave() { return corEMarcaAeronave; }
        public void setCorEMarcaAeronave(String corEMarcaAeronave) { this.corEMarcaAeronave = corEMarcaAeronave; }
        
        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
        
        public String getPilotoEmComando() { return pilotoEmComando; }
        public void setPilotoEmComando(String pilotoEmComando) { this.pilotoEmComando = pilotoEmComando; }
    }
    
    // Construtor padrão
    public PlanoDeVooDTO() {}
    
    // Getters e Setters básicos - apenas os principais campos
    public String getIdentificacaoDaAeronave() { return identificacaoDaAeronave; }
    public void setIdentificacaoDaAeronave(String identificacaoDaAeronave) { this.identificacaoDaAeronave = identificacaoDaAeronave; }
    
    public Boolean getIndicativoDeChamada() { return indicativoDeChamada; }
    public void setIndicativoDeChamada(Boolean indicativoDeChamada) { this.indicativoDeChamada = indicativoDeChamada; }
    
    public String getRegraDeVooEnum() { return regraDeVooEnum; }
    public void setRegraDeVooEnum(String regraDeVooEnum) { this.regraDeVooEnum = regraDeVooEnum; }
    
    public String getTipoDeVooEnum() { return tipoDeVooEnum; }
    public void setTipoDeVooEnum(String tipoDeVooEnum) { this.tipoDeVooEnum = tipoDeVooEnum; }
    
    public Integer getNumeroDeAeronaves() { return numeroDeAeronaves; }
    public void setNumeroDeAeronaves(Integer numeroDeAeronaves) { this.numeroDeAeronaves = numeroDeAeronaves; }
    
    public String getTipoDeAeronave() { return tipoDeAeronave; }
    public void setTipoDeAeronave(String tipoDeAeronave) { this.tipoDeAeronave = tipoDeAeronave; }
    
    public List<String> getEquipamentoCapacidadeDaAeronave() { return equipamentoCapacidadeDaAeronave; }
    public void setEquipamentoCapacidadeDaAeronave(List<String> equipamentoCapacidadeDaAeronave) { this.equipamentoCapacidadeDaAeronave = equipamentoCapacidadeDaAeronave; }
    
    public String getAerodromoDePartida() { return aerodromoDePartida; }
    public void setAerodromoDePartida(String aerodromoDePartida) { this.aerodromoDePartida = aerodromoDePartida; }
    
    public Object getInfoVoo() { return infoVoo; }
    public void setInfoVoo(Object infoVoo) { this.infoVoo = infoVoo; }
    
    public InformacaoSuplementar getInformacaoSuplementar() { return informacaoSuplementar; }
    public void setInformacaoSuplementar(InformacaoSuplementar informacaoSuplementar) { this.informacaoSuplementar = informacaoSuplementar; }
    
    // Métodos faltantes para compilar
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    
    public OffsetDateTime getHoraPartida() { return horaPartida; }
    public void setHoraPartida(OffsetDateTime horaPartida) { this.horaPartida = horaPartida; }
    
    public String getTempoDeVooPrevisto() { return tempoDeVooPrevisto; }
    public void setTempoDeVooPrevisto(String tempoDeVooPrevisto) { this.tempoDeVooPrevisto = tempoDeVooPrevisto; }
    
    public OutrasInformacoes getOutrasInformacoes() { return outrasInformacoes; }
    public void setOutrasInformacoes(OutrasInformacoes outrasInformacoes) { this.outrasInformacoes = outrasInformacoes; }
    
    public String getRota() { return rota; }
    public void setRota(String rota) { this.rota = rota; }
    
    public String getCategoriaEsteiraTurbulenciaEnum() { return categoriaEsteiraTurbulenciaEnum; }
    public void setCategoriaEsteiraTurbulenciaEnum(String categoriaEsteiraTurbulenciaEnum) { this.categoriaEsteiraTurbulenciaEnum = categoriaEsteiraTurbulenciaEnum; }
    
    public List<String> getVigilancia() { return vigilancia; }
    public void setVigilancia(List<String> vigilancia) { this.vigilancia = vigilancia; }
    
    public String getAerodromoDeDestino() { return aerodromoDeDestino; }
    public void setAerodromoDeDestino(String aerodromoDeDestino) { this.aerodromoDeDestino = aerodromoDeDestino; }
    
    public String getAerodromoDeAlternativa() { return aerodromoDeAlternativa; }
    public void setAerodromoDeAlternativa(String aerodromoDeAlternativa) { this.aerodromoDeAlternativa = aerodromoDeAlternativa; }
    
    public String getAerodromoDeAlternativaSegundo() { return aerodromoDeAlternativaSegundo; }
    public void setAerodromoDeAlternativaSegundo(String aerodromoDeAlternativaSegundo) { this.aerodromoDeAlternativaSegundo = aerodromoDeAlternativaSegundo; }
    
    public String getVelocidadeDeCruzeiro() { return velocidadeDeCruzeiro; }
    public void setVelocidadeDeCruzeiro(String velocidadeDeCruzeiro) { this.velocidadeDeCruzeiro = velocidadeDeCruzeiro; }
    
    public String getNivelDeVoo() { return nivelDeVoo; }
    public void setNivelDeVoo(String nivelDeVoo) { this.nivelDeVoo = nivelDeVoo; }
}


