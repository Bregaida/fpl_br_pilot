package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for chart header information.
 */
public record CartasHeaderDto(
    String emenda,
    LocalDateTime lastupdate,
    Integer total
) {}
