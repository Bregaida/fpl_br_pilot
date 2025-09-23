package br.com.fplbr.pilot.piloto.infrastructure.persistence;

import br.com.fplbr.pilot.piloto.domain.entity.Piloto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PilotoRepository implements PanacheRepository<Piloto> {
    
    public Piloto findByCodigoAnac(String codigoAnac) {
        return find("codigoAnac", codigoAnac).firstResult();
    }
    
    public boolean existsByCodigoAnac(String codigoAnac) {
        return count("codigoAnac", codigoAnac) > 0;
    }
}
