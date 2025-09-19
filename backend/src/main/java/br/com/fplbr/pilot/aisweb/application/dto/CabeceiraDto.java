package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para cabeceira de pista.
 */
public record CabeceiraDto(
    String ident,
    List<IluminacaoAerodromoDto> luzes
) {}
