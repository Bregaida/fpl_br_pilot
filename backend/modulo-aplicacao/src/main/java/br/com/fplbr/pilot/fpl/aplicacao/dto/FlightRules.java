package br.com.fplbr.pilot.fpl.aplicacao.dto;

/**
 * Regras de voo para o Item 8 (ATS):
 * I=IFR, V=VFR, Y=IFR depois VFR, Z=VFR depois IFR.
 */
public enum FlightRules {
    IFR, VFR, Y, Z
}
