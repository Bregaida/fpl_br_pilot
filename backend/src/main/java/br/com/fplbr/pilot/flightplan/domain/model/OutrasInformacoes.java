package br.com.fplbr.pilot.flightplan.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OutrasInformacoes {
    private String sts;
    private String pbn;
    private String nav;
    private String com;
    private String dat;
    private String sur;
    private String dep;
    private String dest;
    private String reg;
    private String eet;
    private String sel;
    private String typ;
    private String code;
    private String dle;
    private String opr;
    private String orgn;
    private String per;
    private String altn;
    private String ralt;
    private String talt;
    private String rif;
    private String rmk;
    private String from;

    /**
     * Verifica se todas as informaÃƒÂ§ÃƒÂµes estÃƒÂ£o em branco (nulas ou vazias).
     * @return true se todas as informaÃƒÂ§ÃƒÂµes estiverem em branco, false caso contrÃƒÂ¡rio
     */
    public boolean isBlank() {
        return (sts == null || sts.trim().isEmpty()) &&
               (pbn == null || pbn.trim().isEmpty()) &&
               (nav == null || nav.trim().isEmpty()) &&
               (com == null || com.trim().isEmpty()) &&
               (dat == null || dat.trim().isEmpty()) &&
               (sur == null || sur.trim().isEmpty()) &&
               (dep == null || dep.trim().isEmpty()) &&
               (dest == null || dest.trim().isEmpty()) &&
               (reg == null || reg.trim().isEmpty()) &&
               (eet == null || eet.trim().isEmpty()) &&
               (sel == null || sel.trim().isEmpty()) &&
               (typ == null || typ.trim().isEmpty()) &&
               (code == null || code.trim().isEmpty()) &&
               (dle == null || dle.trim().isEmpty()) &&
               (opr == null || opr.trim().isEmpty()) &&
               (orgn == null || orgn.trim().isEmpty()) &&
               (per == null || per.trim().isEmpty()) &&
               (altn == null || altn.trim().isEmpty()) &&
               (ralt == null || ralt.trim().isEmpty()) &&
               (talt == null || talt.trim().isEmpty()) &&
               (rif == null || rif.trim().isEmpty()) &&
               (rmk == null || rmk.trim().isEmpty()) &&
               (from == null || from.trim().isEmpty());
    }
}
