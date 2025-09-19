package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para dados AIP (pub type=AIP).
 */
public record PubAipDto(
    List<PubAipItemDto> items
) {}
