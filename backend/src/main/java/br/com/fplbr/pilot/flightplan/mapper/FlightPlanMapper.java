package br.com.fplbr.pilot.flightplan.infrastructure.mapper;

import br.com.fplbr.pilot.flightplan.infrastructure.web.dto.FlightPlanDTO;
import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;

@ApplicationScoped
@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FlightPlanMapper {
    
    FlightPlan toEntity(FlightPlanDTO dto);
    
    FlightPlanDTO toDTO(FlightPlan entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDTO(FlightPlanDTO dto, @MappingTarget FlightPlan entity);

    @AfterMapping
    default void setDefaultValues(FlightPlanDTO dto, @MappingTarget FlightPlan entity) {
        if (entity.getDof() == null) {
            entity.setDof(java.time.LocalDate.now());
        }
    }
}
