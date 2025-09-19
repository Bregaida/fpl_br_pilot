package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para dados INFOTEMP (Situação do Aeródromo).
 */
public record InfotempDto(
    Integer totalRegistros,
    List<InfotempItemDto> itens,
    List<InfotempAgregadoPorIcaoDto> agregadoPorIcao,
    InfotempMetaDto meta
) {
    
    /**
     * DTO for individual INFOTEMP items.
     */
    public record InfotempItemDto(
        String id,
        String icao,
        String observacao,
        LocalDateTime inicioVigencia,
        LocalDateTime fimVigencia,
        LocalDateTime dataPublicacao,
        Integer severidade,
        Boolean ativoAgora,
        String impactoOperacional,
        Boolean periodoInvalido
    ) {}
    
    /**
     * DTO for INFOTEMP metadata.
     */
    public record InfotempMetaDto(
        Integer totalItens,
        Integer pagina,
        Integer itensPorPagina
    ) {}
}
