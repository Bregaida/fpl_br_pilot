package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for NOTAM items.
 */
public record NotamItemDto(
    String id,
    String icaoCode,
    String message,
    String type,
    LocalDateTime validFrom,
    LocalDateTime validUntil,
    String fir,
    String trafficType,
    String purpose,
    String scope,
    String lowerLimit,
    String upperLimit,
    String location,
    String status,
    String rawData
) {}
