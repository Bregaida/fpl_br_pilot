package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO for INFOTEMP metadata.
 */
public record InfotempMetaDto(
    Integer totalItens,
    Integer pagina,
    Integer itensPorPagina
) {}
