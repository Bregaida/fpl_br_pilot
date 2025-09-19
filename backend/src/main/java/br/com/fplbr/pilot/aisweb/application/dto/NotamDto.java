package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO for NOTAM data.
 */
public record NotamDto(
    NotamMetaDto meta,
    List<NotamItemDto> items
) {}
