package br.com.fplbr.pilot.flightplan.infrastructure.persistence;

import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightPlanRepository implements PanacheRepository<FlightPlan> {
    
    // MÃƒÂ©todos personalizados podem ser adicionados aqui, se necessÃƒÂ¡rio
    // Por exemplo, consultas especÃƒÂ­ficas que nÃƒÂ£o sÃƒÂ£o cobertas pelos mÃƒÂ©todos padrÃƒÂ£o do Panache
    
    public FlightPlan findByAircraftRegistration(String aircraftRegistration) {
        return find("aircraftRegistration", aircraftRegistration).firstResult();
    }
    
    // Outros mÃƒÂ©todos personalizados podem ser adicionados conforme necessÃƒÂ¡rio
}
