package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO for NOTAM metadata.
 */
public record NotamMetaDto(
    Integer total,
    Integer page,
    Integer perPage,
    String sortBy,
    String sortOrder
) {}
