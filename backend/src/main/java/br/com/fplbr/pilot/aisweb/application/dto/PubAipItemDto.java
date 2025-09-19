package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDate;

/**
 * DTO for AIP (Aeronautical Information Publication) items.
 */
public record PubAipItemDto(
    String groupId,
    String packageName,
    LocalDate amdtEffective,
    String publicId,
    String ext,
    String p,
    String amdtNumber,
    Integer amdtSequence,
    Integer amdtYear,
    Integer variantIndex
) {}
