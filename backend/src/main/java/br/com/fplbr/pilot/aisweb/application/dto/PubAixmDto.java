package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO para dados AIXM (pub type=AIXM).
 */
public record PubAixmDto(
    List<PubAixmItemDto> items
) {}
