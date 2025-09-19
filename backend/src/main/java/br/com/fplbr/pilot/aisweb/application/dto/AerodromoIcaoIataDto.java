package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta da API de aeródromos ICAO/IATA
 */
public record AerodromoIcaoIataDto(
    Long id,
    Long ciadId,
    String ciad,
    String tipoAerodromo,
    String icao,
    String iata,
    String nomeAerodromo,
    String cidadeAerodromo,
    String ufAerodromo,
    LocalDateTime dataAtualizacao,
    LocalDateTime dataPublicacao
) {}
