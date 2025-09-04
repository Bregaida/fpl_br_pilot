package br.com.fplbr.pilot.aerodromos.ports.out;

import br.com.fplbr.pilot.aerodromos.dto.CartaAerodromoDTO;
import java.util.List;

public interface CartaRepositoryPort {
    void upsertAll(List<CartaAerodromoDTO> cartas);
    List<CartaAerodromoDTO> porIcao(String icao);
}
