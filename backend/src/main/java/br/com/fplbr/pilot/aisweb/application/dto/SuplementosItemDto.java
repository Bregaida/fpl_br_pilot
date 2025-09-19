package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDate;

/**
 * DTO for supplement items.
 */
public record SuplementosItemDto(
    String id,
    String icaoCode,
    String type,
    String category,
    String name,
    String description,
    LocalDate effectiveDate,
    LocalDate expirationDate,
    String status,
    String url,
    String fileExtension,
    Long fileSize,
    String createdBy,
    LocalDate createdAt,
    String updatedBy,
    LocalDate updatedAt,
    // Novos campos importantes
    String titulo,
    String texto,
    String duracao,
    String ref,
    String anexo,
    String n,
    String serie,
    String local,
    String dt,
    String dataInicio,
    String dataFim
) {}
