package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO para distância declarada.
 */
public record DistanciaDeclaradaDto(
    String pista,
    Integer tora,
    Integer toda,
    Integer asda,
    Integer lda,
    Double lat,
    Double lng
) {}
