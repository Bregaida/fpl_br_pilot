package br.com.fplbr.pilot.aerodromos.application.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar uma carta aeronÃƒÂ¡utica de um aerÃƒÂ³dromo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartaAerodromoDTO {
    // Fields
    private String icao;
    private String tipo;
    private String titulo;
    private String href;
    private LocalDate dofValidoDe;
    private LocalDate dofValidoAte;
}
