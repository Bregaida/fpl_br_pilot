package br.com.fplbr.pilot.flightplan.infrastructure.persistence;

import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightPlanRepository implements PanacheRepository<FlightPlan> {
    
    // Métodos personalizados podem ser adicionados aqui, se necessário
    // Por exemplo, consultas específicas que não são cobertas pelos métodos padrão do Panache
    
    public FlightPlan findByAircraftRegistration(String aircraftRegistration) {
        return find("aircraftRegistration", aircraftRegistration).firstResult();
    }
    
    // Outros métodos personalizados podem ser adicionados conforme necessário
}
