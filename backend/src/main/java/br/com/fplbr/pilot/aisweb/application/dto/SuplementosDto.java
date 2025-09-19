package br.com.fplbr.pilot.aisweb.application.dto;

import br.com.fplbr.pilot.aisweb.application.dto.SuplementosItemDto;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosMetaDto;

import java.util.List;

/**
 * DTO for supplements data.
 */
public record SuplementosDto(
    SuplementosMetaDto meta,
    List<SuplementosItemDto> items
) {}
