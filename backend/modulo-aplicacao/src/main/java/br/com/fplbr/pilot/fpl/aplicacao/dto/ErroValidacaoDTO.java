package br.com.fplbr.pilot.fpl.aplicacao.dto;

public class ErroValidacaoDTO {
    public String codigo;
    public String mensagem;

    public ErroValidacaoDTO() {}

    public ErroValidacaoDTO(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
}



