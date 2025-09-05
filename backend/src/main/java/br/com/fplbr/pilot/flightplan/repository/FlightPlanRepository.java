package br.com.fplbr.pilot.flightplan.infrastructure.persistence;

import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightPlanRepository implements PanacheRepository<FlightPlan> {
    
    // MÃ©todos personalizados podem ser adicionados aqui, se necessÃ¡rio
    // Por exemplo, consultas especÃ­ficas que nÃ£o sÃ£o cobertas pelos mÃ©todos padrÃ£o do Panache
    
    public FlightPlan findByAircraftRegistration(String aircraftRegistration) {
        return find("aircraftRegistration", aircraftRegistration).firstResult();
    }
    
    // Outros mÃ©todos personalizados podem ser adicionados conforme necessÃ¡rio
}
