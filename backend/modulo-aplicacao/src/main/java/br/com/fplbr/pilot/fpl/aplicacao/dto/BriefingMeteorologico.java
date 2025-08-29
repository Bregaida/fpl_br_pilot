package br.com.fplbr.pilot.fpl.aplicacao.dto;

import java.util.List;

public record BriefingMeteorologico(
        String metar,
        String taf,
        List<String> sigmet
) {}




