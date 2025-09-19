package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for individual INFOTEMP items.
 */
public record InfotempItemDto(
    String id,
    String icao,
    String observacao,
    LocalDateTime inicioVigencia,
    LocalDateTime fimVigencia,
    LocalDateTime dataPublicacao,
    Integer severidade,
    Boolean ativoAgora,
    String impactoOperacional,
    Boolean periodoInvalido
) {}
