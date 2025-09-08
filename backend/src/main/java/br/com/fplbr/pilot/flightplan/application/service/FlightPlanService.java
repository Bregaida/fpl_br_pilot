package br.com.fplbr.pilot.flightplan.application.service;

import br.com.fplbr.pilot.flightplan.infrastructure.web.dto.FlightPlanDTO;
import br.com.fplbr.pilot.flightplan.infrastructure.mapper.FlightPlanMapper;
import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import br.com.fplbr.pilot.flightplan.infrastructure.persistence.FlightPlanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FlightPlanService {

    private final FlightPlanRepository flightPlanRepository;
    private final FlightPlanMapper flightPlanMapper;

    @Inject
    public FlightPlanService(FlightPlanRepository flightPlanRepository, FlightPlanMapper flightPlanMapper) {
        this.flightPlanRepository = flightPlanRepository;
        this.flightPlanMapper = flightPlanMapper;
    }

    @Transactional
    public FlightPlanDTO createFlightPlan(FlightPlanDTO flightPlanDTO) {
        FlightPlan flightPlan = flightPlanMapper.toEntity(flightPlanDTO);
        flightPlanRepository.persist(flightPlan);
        return flightPlanMapper.toDTO(flightPlan);
    }

    @Transactional
    public FlightPlanDTO updateFlightPlan(Long id, FlightPlanDTO flightPlanDTO) {
        FlightPlan flightPlan = flightPlanRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Flight Plan not found with id: " + id));

        flightPlanMapper.updateEntityFromDTO(flightPlanDTO, flightPlan);
        flightPlanRepository.persist(flightPlan);

        return flightPlanMapper.toDTO(flightPlan);
    }

    @Transactional
    public void deleteFlightPlan(Long id) {
        boolean deleted = flightPlanRepository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Flight Plan not found with id: " + id);
        }
    }

    public FlightPlanDTO getFlightPlanById(Long id) {
        FlightPlan flightPlan = flightPlanRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Flight Plan not found with id: " + id));

        return flightPlanMapper.toDTO(flightPlan);
    }

    public List<FlightPlanDTO> getAllFlightPlans() {
        return flightPlanRepository.listAll().stream()
                .map(flightPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FlightPlanDTO> getFlightPlansByPilot(String pilotName) {
        return flightPlanRepository.list("pilotName", pilotName).stream()
                .map(flightPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FlightPlanDTO> getFlightPlansByAircraft(String aircraftRegistration) {
        return flightPlanRepository.list("aircraftRegistration", aircraftRegistration).stream()
                .map(flightPlanMapper::toDTO)
                .collect(Collectors.toList());
    }
}
