package br.com.fplbr.pilot.flightplan.domain.model;

import jakarta.persistence.Embeddable;

import java.util.*;
import java.util.stream.Collectors;

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
    private String ops;

    /**
     * Verifica se todas as informações estão em branco (nulas ou vazias).
     * @return true se todas as informações estiverem em branco, false caso contrário
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
    
    // Construtor padrão
    public OutrasInformacoes() {}
    
    // Getters e Setters básicos
    public String getSts() { return sts; }
    public void setSts(String sts) { this.sts = sts; }
    public String getPbn() { return pbn; }
    public void setPbn(String pbn) { this.pbn = pbn; }
    public String getNav() { return nav; }
    public void setNav(String nav) { this.nav = nav; }
    public String getCom() { return com; }
    public void setCom(String com) { this.com = com; }
    public String getDat() { return dat; }
    public void setDat(String dat) { this.dat = dat; }
    public String getSur() { return sur; }
    public void setSur(String sur) { this.sur = sur; }
    public String getDep() { return dep; }
    public void setDep(String dep) { this.dep = dep; }
    public String getDle() { return dle; }
    public void setDle(String dle) { this.dle = dle; }
    public String getOps() { return ops; }
    public void setOps(String ops) { this.ops = ops; }
    public String getRmk() { return rmk; }
    public void setRmk(String rmk) { this.rmk = rmk; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
}
