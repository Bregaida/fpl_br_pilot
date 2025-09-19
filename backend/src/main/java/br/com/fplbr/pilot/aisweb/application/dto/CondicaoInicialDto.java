package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO for initial TAF conditions.
 */
public record CondicaoInicialDto(
    VentoDto vento,
    Integer visibilidade,
    List<NuvemDto> nuvens
) {}
