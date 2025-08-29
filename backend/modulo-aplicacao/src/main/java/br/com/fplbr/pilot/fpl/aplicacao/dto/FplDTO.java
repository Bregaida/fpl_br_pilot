package br.com.fplbr.pilot.fpl.aplicacao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
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

  // ===== Campo 19 =====
  @Valid public SupplementaryInformation supp;

  // ===== Novos campos para o builder FPL =====
  // Item 7-8
  public String identificacaoAeronave;
  public String regrasDeVoo;
  public String tipoDeVoo;
  public String numeroAeronaves;
  public String tipoAeronave;
  
  // Item 9
  public String esteiraDeTurbulencia;
  
  // Item 10
  public String equipamentoA;
  public String equipamentoB;
  
  // Item 13
  public String aerodromoPartida;
  public String horaPartidaUTC;
  
  // Item 15
  public String velocidadeCruzeiro;
  public String nivelCruzeiro;
  public String rota;
  
  // Item 16
  public String aerodromoDestino;
  public String tempoTotalVoo;
  public List<String> alternativos = new ArrayList<>();
  
  // Item 18
  public String dataDOF;
  public String operadorOPR;
  public String pbn;
  public String nav;
  public String com;
  public String dat;
  public String sur;
  public String rmk;
  
  // Item 19
  public String autonomiaE;
  public String pessoasP;
  public String equipamentosEmergenciaR;
  public String equipamentosSalvamentoS;
  public String coletesA;
  public String jangadasD;
  public String corEMarcacoesC;
  public String contatoTelefoneN;
  
  // Helper para valores padrão
  public static String nz(String v, String d) {
    return (v == null || v.isBlank()) ? d : v.trim();
  }
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

  // ===== Getters e Setters =====
  
  // Item 7-8
  public String getIdentificacaoAeronave() { return identificacaoAeronave; }
  public void setIdentificacaoAeronave(String v) { this.identificacaoAeronave = v; }
  
  public String getRegrasDeVoo() { return regrasDeVoo; }
  public void setRegrasDeVoo(String v) { this.regrasDeVoo = v; }
  
  public String getTipoDeVoo() { return tipoDeVoo; }
  public void setTipoDeVoo(String v) { this.tipoDeVoo = v; }
  
  // Item 9
  public String getNumeroAeronaves() { return numeroAeronaves; }
  public void setNumeroAeronaves(String v) { this.numeroAeronaves = v; }
  
  public String getTipoAeronave() { return tipoAeronave; }
  public void setTipoAeronave(String v) { this.tipoAeronave = v; }
  
  public String getEsteiraDeTurbulencia() { return esteiraDeTurbulencia; }
  public void setEsteiraDeTurbulencia(String v) { this.esteiraDeTurbulencia = v; }
  
  // Item 10
  public String getEquipamentoA() { return equipamentoA; }
  public void setEquipamentoA(String v) { this.equipamentoA = v; }
  
  public String getEquipamentoB() { return equipamentoB; }
  public void setEquipamentoB(String v) { this.equipamentoB = v; }
  
  // Item 13
  public String getAerodromoPartida() { return aerodromoPartida; }
  public void setAerodromoPartida(String v) { this.aerodromoPartida = v; }
  
  public String getHoraPartidaUTC() { return horaPartidaUTC; }
  public void setHoraPartidaUTC(String v) { this.horaPartidaUTC = v; }
  
  // Item 15
  public String getVelocidadeCruzeiro() { return velocidadeCruzeiro; }
  public void setVelocidadeCruzeiro(String v) { this.velocidadeCruzeiro = v; }
  
  public String getNivelCruzeiro() { return nivelCruzeiro; }
  public void setNivelCruzeiro(String v) { this.nivelCruzeiro = v; }
  
  public String getRota() { return rota; }
  public void setRota(String v) { this.rota = v; }
  
  // Item 16
  public String getAerodromoDestino() { return aerodromoDestino; }
  public void setAerodromoDestino(String v) { this.aerodromoDestino = v; }
  
  public String getTempoTotalVoo() { return tempoTotalVoo; }
  public void setTempoTotalVoo(String v) { this.tempoTotalVoo = v; }
  
  public List<String> getAlternativos() { return alternativos; }
  public void setAlternativos(List<String> v) { this.alternativos = (v != null ? v : new ArrayList<>()); }
  
  // Item 18
  public String getDataDOF() { return dataDOF; }
  public void setDataDOF(String v) { this.dataDOF = v; }
  
  public String getOperadorOPR() { return operadorOPR; }
  public void setOperadorOPR(String v) { this.operadorOPR = v; }
  
  public String getPbn() { return pbn; }
  public void setPbn(String v) { this.pbn = v; }
  
  public String getNav() { return nav; }
  public void setNav(String v) { this.nav = v; }
  
  public String getCom() { return com; }
  public void setCom(String v) { this.com = v; }
  
  public String getDat() { return dat; }
  public void setDat(String v) { this.dat = v; }
  
  public String getSur() { return sur; }
  public void setSur(String v) { this.sur = v; }
  
  public String getRmk() { return rmk; }
  public void setRmk(String v) { this.rmk = v; }
  
  // Item 19
  public String getAutonomiaE() { return autonomiaE; }
  public void setAutonomiaE(String v) { this.autonomiaE = v; }
  
  public String getPessoasP() { return pessoasP; }
  public void setPessoasP(String v) { this.pessoasP = v; }
  
  public String getEquipamentosEmergenciaR() { return equipamentosEmergenciaR; }
  public void setEquipamentosEmergenciaR(String v) { this.equipamentosEmergenciaR = v; }
  
  public String getEquipamentosSalvamentoS() { return equipamentosSalvamentoS; }
  public void setEquipamentosSalvamentoS(String v) { this.equipamentosSalvamentoS = v; }
  
  public String getColetesA() { return coletesA; }
  public void setColetesA(String v) { this.coletesA = v; }
  
  public String getJangadasD() { return jangadasD; }
  public void setJangadasD(String v) { this.jangadasD = v; }
  
  public String getCorEMarcacoesC() { return corEMarcacoesC; }
  public void setCorEMarcacoesC(String v) { this.corEMarcacoesC = v; }
  
  public String getContatoTelefoneN() { return contatoTelefoneN; }
  public void setContatoTelefoneN(String v) { this.contatoTelefoneN = v; }
  
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



