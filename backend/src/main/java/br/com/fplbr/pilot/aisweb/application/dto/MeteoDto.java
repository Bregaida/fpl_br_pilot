package br.com.fplbr.pilot.aisweb.application.dto;

import br.com.fplbr.pilot.aisweb.application.dto.MetarDto;
import br.com.fplbr.pilot.aisweb.application.dto.SunDto;
import br.com.fplbr.pilot.aisweb.application.dto.TafDto;

/**
 * DTO para dados meteorológicos agregados.
 */
public record MeteoDto(
    String icao,
    SunDto sol,
    MetarDto metar,
    TafDto taf
) {}
