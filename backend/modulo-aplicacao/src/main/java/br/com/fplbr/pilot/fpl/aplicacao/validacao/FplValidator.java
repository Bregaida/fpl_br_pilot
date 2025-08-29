package br.com.fplbr.pilot.fpl.aplicacao.validacao;

import br.com.fplbr.pilot.fpl.aplicacao.dto.FplDTO;
import br.com.fplbr.pilot.fpl.aplicacao.dto.FplDTO.EquipA;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;

public class FplValidator implements ConstraintValidator<ValidFpl, FplDTO> {

  @Override
  public boolean isValid(FplDTO d, ConstraintValidatorContext ctx) {
    if (d == null) return true;

    boolean ok = true;
    ctx.disableDefaultConstraintViolation();

    // 10a: "N" sozinho
    if (d.equipA != null && d.equipA.contains(EquipA.N) && d.equipA.size() > 1) {
      ok = violation(ctx, "equipA", "\"N\" (nenhum) deve ser o Ãºnico item em 10a") && ok;
    }

    // 10a: "R" -> PBN/
    if (d.equipA != null && d.equipA.contains(EquipA.R)) {
      if (d.other == null || isBlank(d.other.pbn)) {
        ok = violation(ctx, "other.pbn", "PBN/ Ã© obrigatÃ³rio quando 10a contÃ©m \"R\"") && ok;
      }
    }

    // 10a: "Z" -> NAV/|COM/|DAT/
    if (d.equipA != null && d.equipA.contains(EquipA.Z)) {
      if (d.other == null || d.other.extra == null ||
          !(d.other.extra.containsKey("NAV") || d.other.extra.containsKey("COM") || d.other.extra.containsKey("DAT"))) {
        ok = violation(ctx, "other.extra", "NAV/ e/ou COM/ e/ou DAT/ obrigatÃ³rio quando 10a contÃ©m \"Z\"") && ok;
      }
    }

    // AerÃ³dromos ZZZZ exigem DEP/ DEST/ ALTN/
    if ("ZZZZ".equals(d.departureAerodrome)) {
      ok = requireKey(d, ctx, "DEP", "Campo 13 = ZZZZ exige DEP/<localizaÃ§Ã£o> no Campo 18") && ok;
    }
    if ("ZZZZ".equals(d.destinationAerodrome)) {
      ok = requireKey(d, ctx, "DEST", "Campo 16 = ZZZZ exige DEST/<localizaÃ§Ã£o> no Campo 18") && ok;
    }
    if (notBlank(d.alternate1) && "ZZZZ".equals(d.alternate1)) {
      ok = requireKey(d, ctx, "ALTN", "Alternado = ZZZZ exige ALTN/<localizaÃ§Ã£o> no Campo 18") && ok;
    }
    if (notBlank(d.alternate2) && "ZZZZ".equals(d.alternate2)) {
      ok = requireKey(d, ctx, "ALTN", "Alternado = ZZZZ exige ALTN/<localizaÃ§Ã£o> no Campo 18") && ok;
    }

    // Regras de voo x NÃ­vel
    if ("VFR".equals(d.level) && d.flightRules != null &&
        !(d.flightRules == FplDTO.FlightRules.V || d.flightRules == FplDTO.FlightRules.Z)) {
      ok = violation(ctx, "level", "Level VFR exige regras V ou Z") && ok;
    }
    if ("VFR".equals(d.level) && d.flightRules == FplDTO.FlightRules.I) {
      ok = violation(ctx, "level", "IFR nÃ£o pode ter level VFR") && ok;
    }

    // Pessoas a bordo numÃ©rico >=1
    if (d.supp != null && d.supp.personsOnBoard != null &&
        d.supp.personsOnBoard.matches("\\d+")) {
      int pob = Integer.parseInt(d.supp.personsOnBoard);
      if (pob < 1) ok = violation(ctx, "supp.personsOnBoard", "Pessoas a bordo deve ser >= 1") && ok;
    }

    // SobrevivÃªncia S = nenhum
    if (d.supp != null && d.supp.survivalNone) {
      if (d.supp.surPolar || d.supp.surDesert || d.supp.surMaritime || d.supp.surJungle) {
        ok = violation(ctx, "supp.survivalNone", "Se S=true, P/D/M/J devem ser false") && ok;
      }
    }

    // Coletes: se nÃ£o hÃ¡ coletes, demais flags devem ser false
    if (d.supp != null && !d.supp.jackets) {
      if (d.supp.jacketLight || d.supp.jacketFluorescent || d.supp.jacketUHF || d.supp.jacketVHF) {
        ok = violation(ctx, "supp.jackets", "Sem coletes (J=false) â†’ L/F/U/V devem ser false") && ok;
      }
    }

    // Botes: se nÃºmero == 0/null, zera demais
    if (d.supp != null && d.supp.dinghies != null) {
      Integer n = d.supp.dinghies.number;
      if (n == null || n == 0) {
        if ((d.supp.dinghies.capacity != null && d.supp.dinghies.capacity > 0) ||
            d.supp.dinghies.covered ||
            notBlank(d.supp.dinghies.colour)) {
          ok = violation(ctx, "supp.dinghies", "Sem botes: number=0/null â‡’ capacity=0, covered=false, colour vazio") && ok;
        }
      }
    }

    return ok;
  }

  private boolean requireKey(FplDTO d, ConstraintValidatorContext ctx, String key, String msg) {
    Map<String,String> m = d.other != null ? d.other.extra : null;
    if (m == null || !m.containsKey(key) || isBlank(m.get(key))) {
      return violation(ctx, "other.extra", msg);
    }
    return true;
  }

  private static boolean violation(ConstraintValidatorContext ctx, String field, String msg) {
    ctx.buildConstraintViolationWithTemplate(msg).addPropertyNode(field).addConstraintViolation();
    return false;
  }
  private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
  private static boolean notBlank(String s){ return !isBlank(s); }
}

