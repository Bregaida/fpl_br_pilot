package br.com.fplbr.pilot.aisweb.application.dto;

import br.com.fplbr.pilot.aisweb.application.dto.NuvemDto;
import br.com.fplbr.pilot.aisweb.application.dto.VentoDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for METAR data.
 */
public record MetarDto(
    String raw,
    String tipo,
    LocalDateTime horario,
    VentoDto vento,
    Integer prevailing_m,
    List<NuvemDto> clouds,
    List<String> weather,
    Integer temperatura,
    Integer dewpoint,
    Double qnh,
    String remarks
) {}
