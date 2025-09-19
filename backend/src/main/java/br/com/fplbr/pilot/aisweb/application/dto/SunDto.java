package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for sun data.
 */
public record SunDto(
    String icao,
    LocalDateTime date,
    LocalTime sunrise,
    LocalTime sunset,
    Integer diaSemanaNumero,
    String diaSemanaNome,
    Boolean weekdayOk,
    Double latitude,
    Double longitude,
    Boolean geoOk,
    Boolean intervaloOk
) {}
