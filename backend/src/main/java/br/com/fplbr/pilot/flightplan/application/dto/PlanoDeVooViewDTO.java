package br.com.fplbr.pilot.flightplan.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para visualização de Plano de Voo com PLN codificado
 * Estende PlanoDeVooDTO adicionando o campo plnCodificado
 */
public class PlanoDeVooViewDTO extends PlanoDeVooDTO {
    
    @JsonProperty("plnCodificado")
    private String plnCodificado;
    
    public PlanoDeVooViewDTO() {
        super();
    }
    
    public PlanoDeVooViewDTO(PlanoDeVooDTO original) {
        // Copiar todos os campos do DTO original
        this.setIdentificacaoDaAeronave(original.getIdentificacaoDaAeronave());
        this.setIndicativoDeChamada(original.getIndicativoDeChamada());
        this.setRegraDeVooEnum(original.getRegraDeVooEnum());
        this.setTipoDeVooEnum(original.getTipoDeVooEnum());
        this.setNumeroDeAeronaves(original.getNumeroDeAeronaves());
        this.setTipoDeAeronave(original.getTipoDeAeronave());
        this.setCategoriaEsteiraTurbulenciaEnum(original.getCategoriaEsteiraTurbulenciaEnum());
        this.setEquipamentoCapacidadeDaAeronave(original.getEquipamentoCapacidadeDaAeronave());
        this.setVigilancia(original.getVigilancia());
        this.setAerodromoDePartida(original.getAerodromoDePartida());
        this.setHoraPartida(original.getHoraPartida());
        this.setAerodromoDeDestino(original.getAerodromoDeDestino());
        this.setTempoDeVooPrevisto(original.getTempoDeVooPrevisto());
        this.setAerodromoDeAlternativa(original.getAerodromoDeAlternativa());
        this.setAerodromoDeAlternativaSegundo(original.getAerodromoDeAlternativaSegundo());
        this.setVelocidadeDeCruzeiro(original.getVelocidadeDeCruzeiro());
        this.setNivelDeVoo(original.getNivelDeVoo());
        this.setRota(original.getRota());
        this.setOutrasInformacoes(original.getOutrasInformacoes());
        this.setInformacaoSuplementar(original.getInformacaoSuplementar());
        this.setModo(original.getModo());
        this.setInfoVoo(original.getInfoVoo());
    }
    
    public String getPlnCodificado() {
        return plnCodificado;
    }
    
    public void setPlnCodificado(String plnCodificado) {
        this.plnCodificado = plnCodificado;
    }
}
