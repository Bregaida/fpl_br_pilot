package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para pista.
 */
public record PistaDto(
    String type,
    String ident,
    String surface,
    Integer length,
    Integer width,
    String surface_c,
    List<IluminacaoAerodromoDto> luzes,
    List<CabeceiraDto> cabeceiras
) {}
