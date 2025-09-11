package br.com.fplbr.pilot.rotaer.domain.model;

import java.util.List;
import java.util.Map;

/**
 * Resultado do processamento do ROTAER
 */
public class RotaerProcessResult {
    
    private boolean ok;
    private List<ValidationWarning> warnings;
    private RotaerData json;
    private Map<String, Object> diff;
    private String icao;
    private boolean dryRun;
    private String message;
    
    public RotaerProcessResult() {}
    
    public RotaerProcessResult(boolean ok, List<ValidationWarning> warnings, RotaerData json, 
                              Map<String, Object> diff, String icao, boolean dryRun, String message) {
        this.ok = ok;
        this.warnings = warnings;
        this.json = json;
        this.diff = diff;
        this.icao = icao;
        this.dryRun = dryRun;
        this.message = message;
    }
    
    // Getters and setters
    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }
    
    public List<ValidationWarning> getWarnings() { return warnings; }
    public void setWarnings(List<ValidationWarning> warnings) { this.warnings = warnings; }
    
    public RotaerData getJson() { return json; }
    public void setJson(RotaerData json) { this.json = json; }
    
    public Map<String, Object> getDiff() { return diff; }
    public void setDiff(Map<String, Object> diff) { this.diff = diff; }
    
    public String getIcao() { return icao; }
    public void setIcao(String icao) { this.icao = icao; }
    
    public boolean isDryRun() { return dryRun; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
