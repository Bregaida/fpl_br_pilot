package br.com.fplbr.pilot.aerodromos.ports.in;

import java.time.LocalDate;

public interface SolServicePort {
    record SolInfo(LocalDate data, String diaSemana, String sunrise, String sunset) {}
    SolInfo obterSol(String icao, LocalDate data);
}
