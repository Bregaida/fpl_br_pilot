package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDate;

/**
 * DTO for AIXM (Aeronautical Information Exchange Model) items.
 */
public record PubAixmItemDto(
    String groupId,
    String packageName,
    LocalDate amdtEffective,
    String publicId,
    String ext,
    String p,
    String amdtNumber,
    Integer amdtSequence,
    Integer amdtYear,
    Integer variantIndex,
    String typeNormalized
) {}
