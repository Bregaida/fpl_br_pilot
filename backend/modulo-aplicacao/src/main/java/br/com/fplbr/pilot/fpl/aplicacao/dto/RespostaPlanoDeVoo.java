package br.com.fplbr.pilot.fpl.aplicacao.dto;

import java.util.List;

public class RespostaPlanoDeVoo {
    public String mensagemFpl;
    public List<String> avisos;
    /**
     * Campo antigo, mantido por compatibilidade. Use errosDetalhados.
     */
    @Deprecated
    public List<String> erros;

    /**
     * Lista padronizada de erros com cÃ³digo e mensagem.
     */
    public List<ErroValidacaoDTO> errosDetalhados;
    public RespostaPlanoDeVoo() {}
    public RespostaPlanoDeVoo(String mensagemFpl, List<String> avisos, List<String> erros) {
        this.mensagemFpl = mensagemFpl;
        this.avisos = avisos;
        this.erros = erros;
    }

    public RespostaPlanoDeVoo(String mensagemFpl, List<String> avisos, List<String> erros, List<ErroValidacaoDTO> errosDetalhados) {
        this.mensagemFpl = mensagemFpl;
        this.avisos = avisos;
        this.erros = erros;
        this.errosDetalhados = errosDetalhados;
    }
}




