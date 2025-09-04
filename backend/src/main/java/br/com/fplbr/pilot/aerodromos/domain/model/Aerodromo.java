package br.com.fplbr.pilot.aerodromos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um aeródromo no sistema.
 * Um aeródromo pode ser um aeroporto, heliporto, aeroclube, etc.
 */
public class Aerodromo {
    private String icao;               // Código ICAO do aeródromo (4 letras)
    private String iata;               // Código IATA (opcional, 3 letras)
    private String nome;               // Nome do aeródromo
    private String municipio;          // Município onde está localizado
    private String uf;                 // Unidade Federativa (estado)
    private String regiao;             // Região do Brasil
    private Double latitude;           // Latitude em graus decimais
    private Double longitude;          // Longitude em graus decimais
    private Integer altitudePes;       // Altitude em pés
    private String tipo;               // Tipo de aeródromo (AD, HELIPONTO, etc)
    private String uso;                // Uso (Público, Privado, Misto)
    private String cindacta;           // Código CINDACTA (se aplicável)
    private boolean internacional;      // Se é um aeroporto internacional
    private boolean terminal;           // Se é um aeroporto terminal
    private String horarioFuncionamento; // Horário de funcionamento
    private String telefone;           // Telefone para contato
    private String email;              // E-mail para contato
    private String responsavel;        // Responsável pelo aeródromo
    private List<Pista> pistas;        // Lista de pistas do aeródromo
    private List<Frequencia> frequencias = new ArrayList<>(); // Frequências de rádio
    private String observacoes;        // Observações adicionais
    private boolean ativo;             // Se o aeródromo está ativo

    // Private constructor for builder
    private Aerodromo(Builder builder) {
        this.icao = builder.icao;
        this.iata = builder.iata;
        this.nome = builder.nome;
        this.municipio = builder.municipio;
        this.uf = builder.uf;
        this.regiao = builder.regiao;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.altitudePes = builder.altitudePes;
        this.tipo = builder.tipo;
        this.uso = builder.uso;
        this.cindacta = builder.cindacta;
        this.internacional = builder.internacional;
        this.terminal = builder.terminal;
        this.horarioFuncionamento = builder.horarioFuncionamento;
        this.telefone = builder.telefone;
        this.email = builder.email;
        this.responsavel = builder.responsavel;
        this.pistas = builder.pistas != null ? builder.pistas : new ArrayList<>();
        this.frequencias = builder.frequencias != null ? builder.frequencias : new ArrayList<>();
        this.observacoes = builder.observacoes;
        this.ativo = builder.ativo;
    }

    // Default constructor
    public Aerodromo() {
        this.pistas = new ArrayList<>();
        this.frequencias = new ArrayList<>();
    }

    // Builder class
    public static class Builder {
        // Required parameters
        private final String icao;
        private final String nome;

        // Optional parameters - initialized to default values
        private String iata;
        private String municipio;
        private String uf;
        private String regiao;
        private Double latitude;
        private Double longitude;
        private Integer altitudePes;
        private String tipo;
        private String uso;
        private String cindacta;
        private boolean internacional;
        private boolean terminal;
        private String horarioFuncionamento;
        private String telefone;
        private String email;
        private String responsavel;
        private List<Pista> pistas;
        private List<Frequencia> frequencias;
        private String observacoes;
        private boolean ativo = true;

        public Builder(String icao, String nome) {
            this.icao = icao;
            this.nome = nome;
        }

        public Builder iata(String val) { iata = val; return this; }
        public Builder municipio(String val) { municipio = val; return this; }
        public Builder uf(String val) { uf = val; return this; }
        public Builder regiao(String val) { regiao = val; return this; }
        public Builder latitude(Double val) { latitude = val; return this; }
        public Builder longitude(Double val) { longitude = val; return this; }
        public Builder altitudePes(Integer val) { altitudePes = val; return this; }
        public Builder tipo(String val) { tipo = val; return this; }
        public Builder uso(String val) { uso = val; return this; }
        public Builder cindacta(String val) { cindacta = val; return this; }
        public Builder internacional(boolean val) { internacional = val; return this; }
        public Builder terminal(boolean val) { terminal = val; return this; }
        public Builder horarioFuncionamento(String val) { horarioFuncionamento = val; return this; }
        public Builder telefone(String val) { telefone = val; return this; }
        public Builder email(String val) { email = val; return this; }
        public Builder responsavel(String val) { responsavel = val; return this; }
        public Builder pistas(List<Pista> val) { pistas = val; return this; }
        public Builder frequencias(List<Frequencia> val) { frequencias = val; return this; }
        public Builder observacoes(String val) { observacoes = val; return this; }
        public Builder ativo(boolean val) { ativo = val; return this; }

        public Aerodromo build() {
            return new Aerodromo(this);
        }
    }

    // Getters and Setters
    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getAltitudePes() {
        return altitudePes;
    }

    public void setAltitudePes(Integer altitudePes) {
        this.altitudePes = altitudePes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getCindacta() {
        return cindacta;
    }

    public void setCindacta(String cindacta) {
        this.cindacta = cindacta;
    }

    public boolean isInternacional() {
        return internacional;
    }

    public void setInternacional(boolean internacional) {
        this.internacional = internacional;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public List<Pista> getPistas() {
        return pistas;
    }

    public void setPistas(List<Pista> pistas) {
        this.pistas = pistas;
    }

    public List<Frequencia> getFrequencias() {
        return frequencias;
    }

    public void setFrequencias(List<Frequencia> frequencias) {
        this.frequencias = frequencias;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
