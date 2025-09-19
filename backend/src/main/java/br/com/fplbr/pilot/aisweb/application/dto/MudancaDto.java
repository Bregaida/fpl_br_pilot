package br.com.fplbr.pilot.aisweb.application.dto;

import java.util.List;

/**
 * DTO for TAF changes.
 */
public record MudancaDto(
    String tipo,
    String janela,
    CondicaoInicialDto condicoes
) {
    
    // Construtor adicional para compatibilidade
    public MudancaDto(String tipo, String janela, Integer probabilidade, VentoDto vento, Integer visibilidade, List<NuvemDto> nuvens) {
        this(tipo, janela, new CondicaoInicialDto(vento, visibilidade, nuvens));
    }
}
