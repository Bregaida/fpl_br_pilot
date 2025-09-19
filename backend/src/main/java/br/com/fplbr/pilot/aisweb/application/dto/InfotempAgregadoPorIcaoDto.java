package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO for INFOTEMP aggregation by ICAO.
 */
public record InfotempAgregadoPorIcaoDto(
    String icao,
    List<InfotempDto.InfotempItemDto> vigentes,
    InfotempDto.InfotempItemDto vigenteMaisSevero,
    List<InfotempDto.InfotempItemDto> proximos,
    List<InfotempDto.InfotempItemDto> historico,
    String estadoAggregado
) {}
