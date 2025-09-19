package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO for wind data.
 */
public record VentoDto(
    Integer direcao,
    Integer velocidade,
    Integer variacaoMin,
    Integer variacaoMax
) {}
