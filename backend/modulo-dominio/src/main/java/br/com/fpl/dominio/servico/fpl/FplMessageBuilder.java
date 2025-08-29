package br.com.fpl.dominio.servico.fpl;

import br.com.fpl.aplicacao.dto.fpl.FplDTO;
import java.util.*;
import java.util.stream.Collectors;

/** Constrói a linha ATS "FPL-..." (modelo simplificado, suficiente para preview/validação). */
public final class FplMessageBuilder {

  private FplMessageBuilder(){}

  public static String toAts(FplDTO d) {
    List<String> parts = new ArrayList<>();

    // (1) Ident + Regras/Tipo
    parts.add("FPL-" + d.aircraftId);
    parts.add(d.flightRules.name() + d.flightType.name());

    // (2) Número/Tipo/Wake
    parts.add(d.numberOfAircraft + d.aircraftType + "/" + d.wakeTurbulence.name());

    // (3) 10a e 10b
    parts.add(join(d.equipA));
    parts.add(join(d.equipB));

    // (4) 13 Partida/Hora
    parts.add(d.departureAerodrome + d.departureTimeUTC);

    // (5) 15 Velocidade/Nível/Rota
    parts.add(d.cruisingSpeed + d.level + " " + d.route.trim());

    // (6) 16 Destino/EET/Alternados
    String altns = (d.alternate1 != null ? " " + d.alternate1 : "") +
                   (d.alternate2 != null ? " " + d.alternate2 : "");
    parts.add(d.destinationAerodrome + d.totalEet + altns);

    // (7) 18 Outras
    parts.add(build18(d));

    // (8) 19 Suplementares (essenciais)
    parts.add(build19(d));

    // Junta com hífens, como no formato AFTN
    return String.join("-", parts).replaceAll("\\s{2,}", " ").trim();
  }

  private static String join(Collection<?> c){
    if (c == null || c.isEmpty()) return "";
    return c.stream().map(Object::toString).collect(Collectors.joining(""));
  }

  private static String build18(FplDTO d){
    List<String> v = new ArrayList<>();
    if (d.other != null) {
      if (nz(d.other.dof)) v.add("DOF/" + d.other.dof);
      if (nz(d.other.eetByPoint)) v.add("EET/" + d.other.eetByPoint);
      if (nz(d.other.operator)) v.add("OPR/" + d.other.operator);
      if (nz(d.other.performance)) v.add("PER/" + d.other.performance);
      if (nz(d.other.pbn)) v.add("PBN/" + d.other.pbn);
      if (d.other.extra != null && !d.other.extra.isEmpty()) {
        d.other.extra.keySet().stream().sorted().forEach(k -> {
          String val = d.other.extra.get(k);
          if (nz(val)) v.add(k + "/" + val);
        });
      }
    }
    return v.isEmpty() ? "" : String.join(" ", v);
  }

  private static String build19(FplDTO d){
    if (d.supp == null) return "";
    List<String> v = new ArrayList<>();
    v.add("E/" + d.supp.endurance);
    v.add("P/" + d.supp.personsOnBoard);

    // Rádio
    String radio = (d.supp.radioUHF ? "U" : "") + (d.supp.radioVHF ? "V" : "") + (d.supp.elt ? "E" : "");
    if (!radio.isEmpty()) v.add("R/" + radio);

    // Sobrevivência
    if (d.supp.survivalNone) {
      v.add("S/");
    } else {
      String s = (d.supp.surPolar ? "P" : "") + (d.supp.surDesert ? "D" : "") +
                 (d.supp.surMaritime ? "M" : "") + (d.supp.surJungle ? "J" : "");
      if (!s.isEmpty()) v.add("S/" + s);
    }

    // Coletes
    if (d.supp.jackets) {
      String j = "J" + (d.supp.jacketLight ? "L" : "") + (d.supp.jacketFluorescent ? "F" : "") +
                 (d.supp.jacketUHF ? "U" : "") + (d.supp.jacketVHF ? "V" : "");
      v.add(j);
    }

    // Botes
    if (d.supp.dinghies != null && d.supp.dinghies.number != null && d.supp.dinghies.number > 0) {
      v.add("D/" + d.supp.dinghies.number);
      if (d.supp.dinghies.capacity != null) v.add("C/" + d.supp.dinghies.capacity);
      v.add("R/" + (d.supp.dinghies.covered ? "Y" : "N"));
      if (nz(d.supp.dinghies.colour)) v.add("COLOUR/" + d.supp.dinghies.colour);
    }

    v.add("RMK/CLR:" + d.supp.aircraftColorAndMarkings.replaceAll("\\s+", "_"));
    v.add("PIC/" + d.supp.pilotInCommand);
    v.add("ANAC1/" + d.supp.anacPilot1);
    if (nz(d.supp.anacPilot2)) v.add("ANAC2/" + d.supp.anacPilot2);
    v.add("TEL/" + d.supp.telephone);

    return String.join(" ", v);
  }

  private static boolean nz(String s){ return s != null && !s.trim().isEmpty(); }
}
