package br.com.fplbr.pilot.common.infrastructure.web.dto;

public class CepResponseDto {
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private boolean erro;
    private String mensagem;

    public CepResponseDto() {}

    public CepResponseDto(String logradouro, String bairro, String cidade, String uf, String cep, boolean erro, String mensagem) {
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.erro = erro;
        this.mensagem = mensagem;
    }

    public static CepResponseDtoBuilder builder() {
        return new CepResponseDtoBuilder();
    }

    public static class CepResponseDtoBuilder {
        private String logradouro;
        private String bairro;
        private String cidade;
        private String uf;
        private String cep;
        private boolean erro;
        private String mensagem;

        public CepResponseDtoBuilder logradouro(String logradouro) {
            this.logradouro = logradouro;
            return this;
        }

        public CepResponseDtoBuilder bairro(String bairro) {
            this.bairro = bairro;
            return this;
        }

        public CepResponseDtoBuilder cidade(String cidade) {
            this.cidade = cidade;
            return this;
        }

        public CepResponseDtoBuilder uf(String uf) {
            this.uf = uf;
            return this;
        }

        public CepResponseDtoBuilder cep(String cep) {
            this.cep = cep;
            return this;
        }

        public CepResponseDtoBuilder erro(boolean erro) {
            this.erro = erro;
            return this;
        }

        public CepResponseDtoBuilder mensagem(String mensagem) {
            this.mensagem = mensagem;
            return this;
        }

        public CepResponseDto build() {
            return new CepResponseDto(logradouro, bairro, cidade, uf, cep, erro, mensagem);
        }
    }

    // Getters and Setters
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public boolean isErro() { return erro; }
    public void setErro(boolean erro) { this.erro = erro; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
