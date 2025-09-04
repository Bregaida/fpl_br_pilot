package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um aeródromo no banco de dados.
 */
@Entity
@Table(name = "aerodromos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Converte esta entidade para o domínio Aerodromo.
     */
    public br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo toDomain() {
        return new br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo.Builder(this.icao, this.nome)
                .iata(this.iata)
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
     * Cria uma nova entidade a partir de um domínio Aerodromo.
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
                                .map(p -> PistaEntity.fromDomain(p, null)) // O aeródromo será definido após a criação
                                .collect(java.util.stream.Collectors.toList()) : null)
                .frequencias(aerodromo.getFrequencias() != null ? 
                        aerodromo.getFrequencias().stream()
                                .map(f -> FrequenciaEntity.fromDomain(f, null)) // O aeródromo será definido após a criação
                                .collect(java.util.stream.Collectors.toList()) : null)
                .observacoes(aerodromo.getObservacoes())
                .ativo(aerodromo.isAtivo())
                .build();
    }
}
