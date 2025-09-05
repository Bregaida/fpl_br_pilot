package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade JPA que representa um aerÃ³dromo no banco de dados.
 */
@Entity
@Table(name = "aerodromos")
public class AerodromoEntity extends PanacheEntityBase {

    @Id
    @Column(name = "icao", length = 4, nullable = false, unique = true)
    private String icao;

    @Column(name = "iata", length = 3)
    private String iata;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "municipio")
    private String municipio;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "regiao")
    private String regiao;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "altitude_pes")
    private Integer altitudePes;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "uso")
    private String uso;

    @Column(name = "cindacta")
    private String cindacta;

    @Column(name = "internacional")
    private boolean internacional;

    @Column(name = "terminal")
    private boolean terminal;

    @Column(name = "horario_funcionamento")
    private String horarioFuncionamento;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "email")
    private String email;

    @Column(name = "responsavel")
    private String responsavel;

    @OneToMany(mappedBy = "aerodromo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PistaEntity> pistas = new ArrayList<>();

    @OneToMany(mappedBy = "aerodromo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FrequenciaEntity> frequencias = new ArrayList<>();

    @Column(name = "observacoes", length = 4000)
    private String observacoes;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    // ===== Getters and Setters =====
    
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

    public List<PistaEntity> getPistas() {
        if (pistas == null) {
            pistas = new ArrayList<>();
        }
        return pistas;
    }

    public void setPistas(List<PistaEntity> pistas) {
        this.pistas = pistas;
    }

    public List<FrequenciaEntity> getFrequencias() {
        if (frequencias == null) {
            frequencias = new ArrayList<>();
        }
        return frequencias;
    }

    public void setFrequencias(List<FrequenciaEntity> frequencias) {
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
    
    // ===== Builder Pattern =====
    
    public static AerodromoEntityBuilder builder() {
        return new AerodromoEntityBuilder();
    }
    
    public static class AerodromoEntityBuilder {
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
        private boolean internacional;
        private boolean terminal;
        private String horarioFuncionamento;
        private String telefone;
        private String email;
        private String responsavel;
        private List<PistaEntity> pistas;
        private List<FrequenciaEntity> frequencias;
        private String observacoes;
        private boolean ativo;
        
        public AerodromoEntityBuilder icao(String icao) { this.icao = icao; return this; }
        public AerodromoEntityBuilder iata(String iata) { this.iata = iata; return this; }
        public AerodromoEntityBuilder nome(String nome) { this.nome = nome; return this; }
        public AerodromoEntityBuilder municipio(String municipio) { this.municipio = municipio; return this; }
        public AerodromoEntityBuilder uf(String uf) { this.uf = uf; return this; }
        public AerodromoEntityBuilder regiao(String regiao) { this.regiao = regiao; return this; }
        public AerodromoEntityBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public AerodromoEntityBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public AerodromoEntityBuilder altitudePes(Integer altitudePes) { this.altitudePes = altitudePes; return this; }
        public AerodromoEntityBuilder tipo(String tipo) { this.tipo = tipo; return this; }
        public AerodromoEntityBuilder uso(String uso) { this.uso = uso; return this; }
        public AerodromoEntityBuilder cindacta(String cindacta) { this.cindacta = cindacta; return this; }
        public AerodromoEntityBuilder internacional(boolean internacional) { this.internacional = internacional; return this; }
        public AerodromoEntityBuilder terminal(boolean terminal) { this.terminal = terminal; return this; }
        public AerodromoEntityBuilder horarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; return this; }
        public AerodromoEntityBuilder telefone(String telefone) { this.telefone = telefone; return this; }
        public AerodromoEntityBuilder email(String email) { this.email = email; return this; }
        public AerodromoEntityBuilder responsavel(String responsavel) { this.responsavel = responsavel; return this; }
        public AerodromoEntityBuilder pistas(List<PistaEntity> pistas) { this.pistas = pistas; return this; }
        public AerodromoEntityBuilder frequencias(List<FrequenciaEntity> frequencias) { this.frequencias = frequencias; return this; }
        public AerodromoEntityBuilder observacoes(String observacoes) { this.observacoes = observacoes; return this; }
        public AerodromoEntityBuilder ativo(boolean ativo) { this.ativo = ativo; return this; }
        
        public AerodromoEntity build() {
            AerodromoEntity entity = new AerodromoEntity();
            entity.setIcao(icao);
            entity.setIata(iata);
            entity.setNome(nome);
            entity.setMunicipio(municipio);
            entity.setUf(uf);
            entity.setRegiao(regiao);
            entity.setLatitude(latitude);
            entity.setLongitude(longitude);
            entity.setAltitudePes(altitudePes);
            entity.setTipo(tipo);
            entity.setUso(uso);
            entity.setCindacta(cindacta);
            entity.setInternacional(internacional);
            entity.setTerminal(terminal);
            entity.setHorarioFuncionamento(horarioFuncionamento);
            entity.setTelefone(telefone);
            entity.setEmail(email);
            entity.setResponsavel(responsavel);
            if (pistas != null) {
                entity.setPistas(pistas);
            }
            if (frequencias != null) {
                entity.setFrequencias(frequencias);
            }
            entity.setObservacoes(observacoes);
            entity.setAtivo(ativo);
            return entity;
        }
    }

    /**
     * Converte esta entidade para o domÃ­nio Aerodromo.
     */
    public br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo toDomain() {
        return br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo.builder()
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
                .pistas(this.pistas != null ? 
                        this.pistas.stream()
                                .map(PistaEntity::toDomain)
                                .collect(java.util.stream.Collectors.toList()) : null)
                .frequencias(this.frequencias != null ? 
                        this.frequencias.stream()
                                .map(FrequenciaEntity::toDomain)
                                .collect(java.util.stream.Collectors.toList()) : null)
                .observacoes(this.observacoes)
                .ativo(this.ativo)
                .build();
    }

    /**
     * Cria uma nova entidade a partir de um domÃ­nio Aerodromo.
     */
    public static AerodromoEntity fromDomain(br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo aerodromo) {
        if (aerodromo == null) {
            return null;
        }

        return AerodromoEntity.builder()
                .icao(aerodromo.getIcao())
                .iata(aerodromo.getIata())
                .nome(aerodromo.getNome())
                .municipio(aerodromo.getMunicipio())
                .uf(aerodromo.getUf())
                .regiao(aerodromo.getRegiao())
                .latitude(aerodromo.getLatitude())
                .longitude(aerodromo.getLongitude())
                .altitudePes(aerodromo.getAltitudePes())
                .tipo(aerodromo.getTipo())
                .uso(aerodromo.getUso())
                .cindacta(aerodromo.getCindacta())
                .internacional(aerodromo.isInternacional())
                .terminal(aerodromo.isTerminal())
                .horarioFuncionamento(aerodromo.getHorarioFuncionamento())
                .telefone(aerodromo.getTelefone())
                .email(aerodromo.getEmail())
                .responsavel(aerodromo.getResponsavel())
                .pistas(aerodromo.getPistas() != null ? 
                        aerodromo.getPistas().stream()
                                .map(p -> PistaEntity.fromDomain(p, null)) // O aerÃ³dromo serÃ¡ definido apÃ³s a criaÃ§Ã£o
                                .collect(java.util.stream.Collectors.toList()) : null)
                .frequencias(aerodromo.getFrequencias() != null ? 
                        aerodromo.getFrequencias().stream()
                                .map(f -> FrequenciaEntity.fromDomain(f, null)) // O aerÃ³dromo serÃ¡ definido apÃ³s a criaÃ§Ã£o
                                .collect(java.util.stream.Collectors.toList()) : null)
                .observacoes(aerodromo.getObservacoes())
                .ativo(aerodromo.isAtivo())
                .build();
    }
}
