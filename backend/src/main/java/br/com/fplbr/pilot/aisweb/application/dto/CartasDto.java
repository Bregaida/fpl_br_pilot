package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para dados de cartas aeronáuticas.
 */
public record CartasDto(
    CartasHeaderDto header,
    List<CartasItemDto> items
) {}
