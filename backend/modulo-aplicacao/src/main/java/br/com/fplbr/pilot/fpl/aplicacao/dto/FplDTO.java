package br.com.fplbr.pilot.fpl.aplicacao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.Set;
import br.com.fplbr.pilot.fpl.aplicacao.validacao.ValidFpl;

/** DTO do Plano de Voo (PVC/PVS) conforme ICAO/DECEA */
@ValidFpl
public class FplDTO {

  // ---- Campo 7
  @NotBlank
  @Pattern(regexp = "^[A-Z0-9]{2,7}$")
  public String aircraftId;

  // ---- Campo 8
  @NotNull public FlightRules flightRules; // I,V,Y,Z
  @NotNull public FlightType flightType;   // S,N,G,M,X

  // ---- Campo 9
  @NotNull @Min(1) @Max(99)
  public Integer numberOfAircraft;

  @NotBlank
  @Pattern(regexp = "^[A-Z0-9]{2,4}$")
  public String aircraftType;

  @NotNull public WakeTurbulence wakeTurbulence; // L,M,H,J

  // ---- Campo 10
  @NotEmpty public Set<EquipA> equipA; // COM/NAV/APPR
  @NotEmpty public Set<EquipB> equipB; // VigilÃ¢ncia

  // ---- Campo 13
  @NotBlank
  @Pattern(regexp = "^(?:[A-Z]{4}|ZZZZ)$")
  public String departureAerodrome;

  @NotBlank
  @Pattern(regexp = "^[0-2][0-9][0-5][0-9]$")
  public String departureTimeUTC; // HHMM

  // ---- Campo 15
  @NotBlank
  @Pattern(regexp = "^(?:N\\d{4}|K\\d{4}|M\\d{3})$")
  public String cruisingSpeed;

  @NotBlank
  @Pattern(regexp = "^(?:F\\d{3}|A\\d{3}|S\\d{4}|VFR)$")
  public String level;

  @NotBlank
  @Size(max = 600)
  @Pattern(regexp = "^[A-Z0-9 .\\/\\-]{1,600}$")
  public String route;

  // ---- Campo 16
  @NotBlank
  @Pattern(regexp = "^(?:[A-Z]{4}|ZZZZ)$")
  public String destinationAerodrome;

  @NotBlank
  @Pattern(regexp = "^[0-2][0-9][0-5][0-9]$")
  public String totalEet; // HHMM

  @Pattern(regexp = "^(?:[A-Z]{4}|ZZZZ)?$")
  public String alternate1;

  @Pattern(regexp = "^(?:[A-Z]{4}|ZZZZ)?$")
  public String alternate2;

  // ---- Campo 18
  @Valid public OtherInformation other;

  // ---- Campo 19
  @Valid public SupplementaryInformation supp;

  // ===== Enums =====
  public enum FlightRules { I, V, Y, Z }
  public enum FlightType { S, N, G, M, X }
  public enum WakeTurbulence { L, M, H, J }

  /** 10a â€“ COM/NAV/APPR */
  public enum EquipA {
    N, S, A, B, C, D, F, G, H, I, J, K, L, M, O, R, T, U, V, W, Y, Z
  }
  /** 10b â€“ VigilÃ¢ncia */
  public enum EquipB {
    N, A, C, E, H, I, L, P, S, X, B1, U1, V1, D1, L1
  }

  // ===== Campo 18 =====
  public static class OtherInformation {
    /** DOF/YYMMDD */
    @Pattern(regexp = "^\\d{6}$")
    public String dof;

    /** EET/AAAAhhmm . (opcional, adicional ao total) */
    public String eetByPoint;

    /** OPR/ */
    public String operator;

    /** PER/[A|B|C|D|E|H] */
    @Pattern(regexp = "^[ABCDEH]$")
    public String performance;

    /** PBN/ (obrigatÃ³rio se EquipA contÃ©m R) */
    public String pbn;

    /** NAV/, COM/, DAT/, REG/, SEL/, RMK/, FROM/, DEP/, DEST/, ALTN/ etc. */
    public Map<String,String> extra; // chave sem barra: "RMK" -> "texto"
  }

  // ===== Campo 19 =====
  public static class SupplementaryInformation {
    /** E/HHMM */
    @NotBlank @Pattern(regexp = "^[0-2][0-9][0-5][0-9]$")
    public String endurance;

    /** P/nnn ou TBN */
    @NotBlank @Pattern(regexp = "^(?:\\d{1,3}|TBN)$")
    public String personsOnBoard;

    // RÃ¡dio emergÃªncia
    public boolean radioUHF; public boolean radioVHF; public boolean elt;

    // SobrevivÃªncia
    public boolean surPolar; public boolean surDesert; public boolean surMaritime; public boolean surJungle;
    /** S = nenhum (se true, todos acima devem ser false) */
    public boolean survivalNone;

    // Coletes
    public boolean jackets; public boolean jacketLight; public boolean jacketFluorescent; public boolean jacketUHF; public boolean jacketVHF;

    // Botes
    public Dinghies dinghies;

    // Cor/Marca, PIC, ANAC, telefone
    @NotBlank public String aircraftColorAndMarkings;
    @NotBlank public String pilotInCommand;
    @NotBlank @Pattern(regexp = "^\\d{1,10}$") public String anacPilot1;
    @Pattern(regexp = "^\\d{0,10}$") public String anacPilot2;
    @NotBlank @Pattern(regexp = "^\\d{8,15}$") public String telephone;
  }

  public static class Dinghies {
    /** Se nenhum bote, deixe null ou 0; covered=false; colour=null */
    @Min(0) public Integer number;      // D/
    @Min(0) public Integer capacity;    // C/
    public boolean covered;             // R/
    public String colour;               // COLOUR/
  }
}


