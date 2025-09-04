package br.com.fplbr.pilot.aerodromos.ports.in;

import br.com.fplbr.pilot.aerodromos.dto.CartaAerodromoDTO;
import java.util.List;

public interface CartasIndexerPort {
    List<CartaAerodromoDTO> indexarDeZip(byte[] zipBytes);
}
