package br.com.fplbr.pilot.aisweb.application.dto;

import br.com.fplbr.pilot.aisweb.application.dto.CondicaoInicialDto;
import br.com.fplbr.pilot.aisweb.application.dto.MudancaDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for TAF (Terminal Aerodrome Forecast) data.
 */
public record TafDto(
    String raw,
    LocalDateTime issued,
    String validity,
    CondicaoInicialDto condicoesIniciais,
    Integer tn,
    Integer tx,
    List<MudancaDto> mudancas
) {}
