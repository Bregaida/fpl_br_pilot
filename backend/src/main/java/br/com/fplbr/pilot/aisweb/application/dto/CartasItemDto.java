package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for chart items.
 */
public record CartasItemDto(
    String id,
    String especie,
    String tipo,
    String tipoDescr,
    String nome,
    String icaoCode,
    LocalDateTime dt,
    LocalDateTime dtPublic,
    String amdt,
    String use,
    Boolean hasSpecialProcedures,
    Integer suplementosCount,
    String downloadUrl,
    String fileSlug,
    String format
) {}
