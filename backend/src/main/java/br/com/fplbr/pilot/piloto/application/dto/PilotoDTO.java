package br.com.fplbr.pilot.piloto.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilotoDTO {

    private Long id;

    @NotBlank(message = "Nome do piloto é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Código ANAC é obrigatório")
    @Pattern(regexp = "^[0-9]+$", message = "Código ANAC deve conter apenas números")
    @Size(max = 20, message = "Código ANAC deve ter no máximo 20 caracteres")
    private String codigoAnac;

    @NotBlank(message = "Nome de pista é obrigatório")
    @Size(max = 50, message = "Nome de pista deve ter no máximo 50 caracteres")
    private String nomePista;
}
