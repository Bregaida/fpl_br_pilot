package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO for supplements metadata.
 */
public record SuplementosMetaDto(
    Integer total,
    Integer page,
    Integer perPage,
    String sortBy,
    String sortOrder
) {}
