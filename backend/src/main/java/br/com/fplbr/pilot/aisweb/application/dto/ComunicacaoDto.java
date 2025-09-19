package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para comunicação.
 */
public record ComunicacaoDto(
    String type,
    String callsign,
    List<String> frequencias
) {}
