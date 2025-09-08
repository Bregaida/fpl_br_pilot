package br.com.fplbr.pilot.aerodromos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um aerÃƒÂ³dromo no sistema.
 * Um aerÃƒÂ³dromo pode ser um aeroporto, heliporto, aeroclube, etc.
 */
public class Aerodromo {
    // Builder pattern implementation
    public static AerodromoBuilder builder() {
        return new AerodromoBuilder();
    }
    
    public static class AerodromoBuilder {
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
        private String observacoes;
        private Boolean ativo = true;
        private List<Pista> pistas = new ArrayList<>();
        private List<Frequencia> frequencias = new ArrayList<>();
        
        public AerodromoBuilder icao(String icao) { this.icao = icao; return this; }
        public AerodromoBuilder iata(String iata) { this.iata = iata; return this; }
        public AerodromoBuilder nome(String nome) { this.nome = nome; return this; }
        public AerodromoBuilder municipio(String municipio) { this.municipio = municipio; return this; }
        public AerodromoBuilder uf(String uf) { this.uf = uf; return this; }
        public AerodromoBuilder regiao(String regiao) { this.regiao = regiao; return this; }
        public AerodromoBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public AerodromoBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public AerodromoBuilder altitudePes(Integer altitudePes) { this.altitudePes = altitudePes; return this; }
        public AerodromoBuilder tipo(String tipo) { this.tipo = tipo; return this; }
        public AerodromoBuilder uso(String uso) { this.uso = uso; return this; }
        public AerodromoBuilder cindacta(String cindacta) { this.cindacta = cindacta; return this; }
        public AerodromoBuilder internacional(Boolean internacional) { this.internacional = internacional; return this; }
        public AerodromoBuilder terminal(Boolean terminal) { this.terminal = terminal; return this; }
        public AerodromoBuilder horarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; return this; }
        public AerodromoBuilder telefone(String telefone) { this.telefone = telefone; return this; }
        public AerodromoBuilder email(String email) { this.email = email; return this; }
        public AerodromoBuilder responsavel(String responsavel) { this.responsavel = responsavel; return this; }
        public AerodromoBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }
        public AerodromoBuilder ativo(Boolean ativo) { this.ativo = ativo; return this; }
        public AerodromoBuilder pistas(List<Pista> pistas) { this.pistas = pistas; return this; }
        public AerodromoBuilder frequencias(List<Frequencia> frequencias) { this.frequencias = frequencias; return this; }
        
        public Aerodromo build() {
            return new Aerodromo(
                icao, iata, nome, municipio, uf, regiao, latitude, longitude, altitudePes,
                tipo, uso, cindacta, internacional, terminal, horarioFuncionamento, telefone,
                email, responsavel, observacoes, ativo, pistas, frequencias
            );
        }
    }
    
    // Fields
    private final String icao;               // CÃƒÂ³digo ICAO do aerÃƒÂ³dromo (4 letras)
    private final String iata;               // CÃƒÂ³digo IATA (opcional, 3 letras)
    private final String nome;               // Nome do aerÃƒÂ³dromo
    private final String municipio;          // MunicÃƒÂ­pio onde estÃƒÂ¡ localizado
    private final String uf;                 // Unidade Federativa (estado)
    private final String regiao;             // RegiÃƒÂ£o do Brasil
    private final Double latitude;           // Latitude em graus decimais
    private final Double longitude;          // Longitude em graus decimais
    private final Integer altitudePes;       // Altitude em pÃƒÂ©s
    private final String tipo;               // Tipo de aerÃƒÂ³dromo (AD, HELIPONTO, etc)
    private final String uso;                // Uso (PÃƒÂºblico, Privado, Misto)
    private final String cindacta;           // CÃƒÂ³digo CINDACTA (se aplicÃƒÂ¡vel)
    private final Boolean internacional;     // Se ÃƒÂ© um aeroporto internacional
    private final Boolean terminal;          // Se ÃƒÂ© um aeroporto terminal
    private String horarioFuncionamento;     // HorÃƒÂ¡rio de funcionamento
    private String telefone;                 // Telefone para contato
    private String email;                    // E-mail para contato
    private String responsavel;              // ResponsÃƒÂ¡vel pelo aerÃƒÂ³dromo
    private String observacoes;              // ObservaÃƒÂ§ÃƒÂµes adicionais
    private Boolean ativo;                   // Se o aerÃƒÂ³dromo estÃƒÂ¡ ativo
    private List<Pista> pistas;              // Lista de pistas do aerÃƒÂ³dromo
    private List<Frequencia> frequencias;    // FrequÃƒÂªncias de rÃƒÂ¡dio
    
    // Constructor
    public Aerodromo(
        String icao, String iata, String nome, String municipio, String uf, String regiao,
        Double latitude, Double longitude, Integer altitudePes, String tipo, String uso,
        String cindacta, Boolean internacional, Boolean terminal, String horarioFuncionamento,
        String telefone, String email, String responsavel, String observacoes, Boolean ativo,
        List<Pista> pistas, List<Frequencia> frequencias
    ) {
        this.icao = icao;
        this.iata = iata;
        this.nome = nome;
        this.municipio = municipio;
        this.uf = uf;
        this.regiao = regiao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudePes = altitudePes;
        this.tipo = tipo;
        this.uso = uso;
        this.cindacta = cindacta;
        this.internacional = internacional;
        this.terminal = terminal;
        this.horarioFuncionamento = horarioFuncionamento;
        this.telefone = telefone;
        this.email = email;
        this.responsavel = responsavel;
        this.observacoes = observacoes;
        this.ativo = ativo != null ? ativo : true;
        this.pistas = pistas != null ? pistas : new ArrayList<>();
        this.frequencias = frequencias != null ? frequencias : new ArrayList<>();
    }
    
    // Copy constructor for toBuilder()
    public Aerodromo(Aerodromo other) {
        this(
            other.icao, other.iata, other.nome, other.municipio, other.uf, other.regiao,
            other.latitude, other.longitude, other.altitudePes, other.tipo, other.uso,
            other.cindacta, other.internacional, other.terminal, other.horarioFuncionamento,
            other.telefone, other.email, other.responsavel, other.observacoes, other.ativo,
            new ArrayList<>(other.pistas), new ArrayList<>(other.frequencias)
        );
    }
    
    // toBuilder method
    public AerodromoBuilder toBuilder() {
        return new AerodromoBuilder()
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
            .internacional(this.internacional)
            .terminal(this.terminal)
            .horarioFuncionamento(this.horarioFuncionamento)
            .telefone(this.telefone)
            .email(this.email)
            .responsavel(this.responsavel)
            .observacoes(this.observacoes)
            .ativo(this.ativo)
            .pistas(new ArrayList<>(this.pistas))
            .frequencias(new ArrayList<>(this.frequencias));
    }
    
    // Getters and setters for all fields
    public String getIcao() { return icao; }
    public String getIata() { return iata; }
    public String getNome() { return nome; }
    public String getMunicipio() { return municipio; }
    public String getUf() { return uf; }
    public String getRegiao() { return regiao; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Integer getAltitudePes() { return altitudePes; }
    public String getTipo() { return tipo; }
    public String getUso() { return uso; }
    public String getCindacta() { return cindacta; }
    public Boolean getInternacional() { return internacional; }
    public Boolean getTerminal() { return terminal; }
    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getResponsavel() { return responsavel; }
    public String getObservacoes() { return observacoes; }
    public Boolean getAtivo() { return ativo; }
    public List<Pista> getPistas() { return new ArrayList<>(pistas); }
    public List<Frequencia> getFrequencias() { return new ArrayList<>(frequencias); }
    
    // Setters for mutable fields
    public void setHorarioFuncionamento(String horarioFuncionamento) { 
        this.horarioFuncionamento = horarioFuncionamento; 
    }
    public void setTelefone(String telefone) { 
        this.telefone = telefone; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public void setResponsavel(String responsavel) { 
        this.responsavel = responsavel; 
    }
    public void setObservacoes(String observacoes) { 
        this.observacoes = observacoes; 
    }
    public void setAtivo(Boolean ativo) { 
        this.ativo = ativo; 
    }
    
    // Collection management
    public void addPista(Pista pista) {
        if (pista != null) {
            this.pistas.add(pista);
        }
    }
    
    public void removePista(Pista pista) {
        this.pistas.remove(pista);
    }
    
    public void addFrequencia(Frequencia frequencia) {
        if (frequencia != null) {
            this.frequencias.add(frequencia);
        }
    }
    
    public void removeFrequencia(Frequencia frequencia) {
        this.frequencias.remove(frequencia);
    }
    
    // Business methods
    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }
    
    public boolean isInternacional() {
        return Boolean.TRUE.equals(internacional);
    }
    
    public boolean isTerminal() {
        return Boolean.TRUE.equals(terminal);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aerodromo aerodromo = (Aerodromo) o;
        return Objects.equals(icao, aerodromo.icao);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(icao);
    }
    
    @Override
    public String toString() {
        return "Aerodromo{" +
                "icao='" + icao + '\'' +
                ", nome='" + nome + '\'' +
                ", municipio='" + municipio + '\'' +
                ", uf='" + uf + '\'' +
                ", ativo=" + ativo +
                '}';
    }
    
    // Remove duplicidades indevidas adicionadas no final do arquivo
}
