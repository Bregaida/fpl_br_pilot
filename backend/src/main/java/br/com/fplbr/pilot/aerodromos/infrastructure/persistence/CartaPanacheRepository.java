package br.com.fplbr.pilot.aerodromos.infrastructure.persistence;

import br.com.fplbr.pilot.aerodromos.application.dto.CartaAerodromoDTO;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartaPanacheRepository implements CartaRepositoryPort {
    private final java.util.concurrent.ConcurrentMap<String, List<CartaAerodromoDTO>> mem = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void upsertAll(List<CartaAerodromoDTO> cartas) {
        Map<String, List<CartaAerodromoDTO>> byIcao = cartas.stream().collect(Collectors.groupingBy(CartaAerodromoDTO::getIcao));
        mem.putAll(byIcao);
    }

    @Override
    public List<CartaAerodromoDTO> porIcao(String icao) {
        return mem.getOrDefault(icao.toUpperCase(), List.of());
    }
}


