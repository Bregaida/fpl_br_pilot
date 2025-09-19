package br.com.fplbr.pilot.aisweb.application.dto;

/**
 * DTO para iluminação do aeródromo.
 */
public record IluminacaoAerodromoDto(
    String codigo,
    String descricao,
    String complemento
) {}
