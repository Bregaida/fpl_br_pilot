package br.com.fplbr.pilot.aisweb.infrastructure.persistence.repository;

import br.com.fplbr.pilot.aisweb.infrastructure.persistence.entity.AerodromoIcaoIataEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repositório para operações de banco de dados da entidade AerodromoIcaoIata
 */
@ApplicationScoped
public class AerodromoIcaoIataRepository implements PanacheRepository<AerodromoIcaoIataEntity> {
    
    /**
     * Busca aeródromo por código ICAO
     */
    public AerodromoIcaoIataEntity findByIcao(String icao) {
        return find("icao", icao).firstResult();
    }
    
    /**
     * Busca aeródromo por código IATA
     */
    public AerodromoIcaoIataEntity findByIata(String iata) {
        return find("iata", iata).firstResult();
    }
    
    /**
     * Verifica se existe aeródromo com o ICAO informado
     */
    public boolean existsByIcao(String icao) {
        return count("icao", icao) > 0;
    }
    
    /**
     * Busca todos os aeródromos de uma UF
     */
    public java.util.List<AerodromoIcaoIataEntity> findByUf(String uf) {
        return find("ufAerodromo", uf).list();
    }
}
