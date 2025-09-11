package br.com.fplbr.pilot.rotaer.domain.model;

/**
 * Representa um warning de validação durante o processamento do ROTAER
 */
public class ValidationWarning {
    
    public enum Severity {
        INFO,    // Informação
        WARNING, // Aviso
        ERROR    // Erro (mas não aborta o processamento)
    }
    
    private String campo;
    private String valor;
    private String regra;
    private Severity severidade;
    private String sugestao;
    
    public ValidationWarning() {}
    
    public ValidationWarning(String campo, String valor, String regra, Severity severidade, String sugestao) {
        this.campo = campo;
        this.valor = valor;
        this.regra = regra;
        this.severidade = severidade;
        this.sugestao = sugestao;
    }
    
    // Getters and setters
    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }
    
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    
    public String getRegra() { return regra; }
    public void setRegra(String regra) { this.regra = regra; }
    
    public Severity getSeveridade() { return severidade; }
    public void setSeveridade(Severity severidade) { this.severidade = severidade; }
    
    public String getSugestao() { return sugestao; }
    public void setSugestao(String sugestao) { this.sugestao = sugestao; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s (valor: %s) - %s", 
                           severidade, campo, regra, valor, sugestao);
    }
}
