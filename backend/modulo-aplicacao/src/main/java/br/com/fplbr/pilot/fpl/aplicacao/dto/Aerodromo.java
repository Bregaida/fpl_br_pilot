package br.com.fplbr.pilot.fpl.aplicacao.dto;

public record Aerodromo(
        String icao,
        String nome,
        String municipio,
        String uf,
        Double latitude,
        Double longitude
) {}




