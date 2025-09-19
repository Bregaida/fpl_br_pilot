package br.com.fplbr.pilot.aisweb.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade para armazenar dados de aeródromos consultados da API AISWEB
 */
@Entity
@Table(name = "aerodromo_icao_iata")
public class AerodromoIcaoIataEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ciad_id", nullable = false)
    private Long ciadId;
    
    @Column(name = "ciad", length = 20)
    private String ciad;
    
    @Column(name = "tipo_aerodromo", length = 50)
    private String tipoAerodromo;
    
    @Column(name = "icao", length = 4, nullable = false, unique = true)
    private String icao;
    
    @Column(name = "iata", length = 3)
    private String iata;
    
    @Column(name = "nome_aerodromo", length = 200)
    private String nomeAerodromo;
    
    @Column(name = "cidade_aerodromo", length = 100)
    private String cidadeAerodromo;
    
    @Column(name = "uf_aerodromo", length = 2)
    private String ufAerodromo;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;
    
    // Construtores
    public AerodromoIcaoIataEntity() {}
    
    public AerodromoIcaoIataEntity(Long ciadId, String ciad, String tipoAerodromo, 
                                  String icao, String iata, String nomeAerodromo, 
                                  String cidadeAerodromo, String ufAerodromo, 
                                  LocalDateTime dataPublicacao) {
        this.ciadId = ciadId;
        this.ciad = ciad;
        this.tipoAerodromo = tipoAerodromo;
        this.icao = icao;
        this.iata = iata;
        this.nomeAerodromo = nomeAerodromo;
        this.cidadeAerodromo = cidadeAerodromo;
        this.ufAerodromo = ufAerodromo;
        this.dataPublicacao = dataPublicacao;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCiadId() {
        return ciadId;
    }
    
    public void setCiadId(Long ciadId) {
        this.ciadId = ciadId;
    }
    
    public String getCiad() {
        return ciad;
    }
    
    public void setCiad(String ciad) {
        this.ciad = ciad;
    }
    
    public String getTipoAerodromo() {
        return tipoAerodromo;
    }
    
    public void setTipoAerodromo(String tipoAerodromo) {
        this.tipoAerodromo = tipoAerodromo;
    }
    
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
    
    public String getNomeAerodromo() {
        return nomeAerodromo;
    }
    
    public void setNomeAerodromo(String nomeAerodromo) {
        this.nomeAerodromo = nomeAerodromo;
    }
    
    public String getCidadeAerodromo() {
        return cidadeAerodromo;
    }
    
    public void setCidadeAerodromo(String cidadeAerodromo) {
        this.cidadeAerodromo = cidadeAerodromo;
    }
    
    public String getUfAerodromo() {
        return ufAerodromo;
    }
    
    public void setUfAerodromo(String ufAerodromo) {
        this.ufAerodromo = ufAerodromo;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }
    
    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
