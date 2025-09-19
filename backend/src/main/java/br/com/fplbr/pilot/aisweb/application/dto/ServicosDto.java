package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para serviços do aeródromo.
 */
public record ServicosDto(
    List<ComunicacaoDto> com,
    String combustivel,
    String servicoAeronave,
    String servicoAeroporto,
    String met,
    String ais,
    String nav
) {}
