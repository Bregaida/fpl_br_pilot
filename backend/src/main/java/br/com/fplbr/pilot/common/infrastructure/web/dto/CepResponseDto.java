package br.com.fplbr.pilot.common.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CepResponseDto {
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private boolean erro;
    private String mensagem;
}
