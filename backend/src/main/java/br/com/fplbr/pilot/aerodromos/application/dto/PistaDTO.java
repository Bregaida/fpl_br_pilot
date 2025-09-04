package br.com.fplbr.pilot.aerodromos.application.dto;

import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representação de uma pista de aeródromo na camada de apresentação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PistaDTO {
    private String designacao;
    private Double comprimentoMetros;
    private Double larguraMetros;
    private String superficie;
    private Integer resistenciaPcn;
    private String classificacaoPcn;
    private Integer tora;
    private Integer toda;
    private Integer asda;
    private Integer lda;
    private Boolean ils;
    private String categoriaIls;
    private String papi;
    private String luzesBorda;
    private String luzesCentro;
    private String observacoes;

    /**
     * Converte uma entidade Pista para PistaDTO.
     */
    public static PistaDTO fromDomain(br.com.fplbr.pilot.aerodromos.domain.model.Pista pista) {
        if (pista == null) {
            return null;
        }

        return PistaDTO.builder()
                .designacao(pista.getDesignacao())
                .comprimentoMetros(pista.getComprimentoMetros())
                .larguraMetros(pista.getLarguraMetros())
                .superficie(pista.getSuperficie())
                .resistenciaPcn(pista.getResistenciaPcn())
                .classificacaoPcn(pista.getClassificacaoPcn())
                .tora(pista.getTora())
                .toda(pista.getToda())
                .asda(pista.getAsda())
                .lda(pista.getLda())
                .ils(pista.isIls())
                .categoriaIls(pista.getCategoriaIls())
                .papi(pista.getPapi())
                .luzesBorda(pista.getLuzesBorda())
                .luzesCentro(pista.getLuzesCentro())
                .observacoes(pista.getObservacoes())
                .build();
    }

    /**
     * Converte este DTO para uma entidade de domínio Pista.
     */
    public br.com.fplbr.pilot.aerodromos.domain.model.Pista toDomain() {
        return br.com.fplbr.pilot.aerodromos.domain.model.Pista.builder()
                .designacao(this.designacao)
                .comprimentoMetros(this.comprimentoMetros)
                .larguraMetros(this.larguraMetros)
                .superficie(this.superficie)
                .resistenciaPcn(this.resistenciaPcn)
                .classificacaoPcn(this.classificacaoPcn)
                .tora(this.tora)
                .toda(this.toda)
                .asda(this.asda)
                .lda(this.lda)
                .ils(this.ils != null && this.ils)
                .categoriaIls(this.categoriaIls)
                .papi(this.papi)
                .luzesBorda(this.luzesBorda)
                .luzesCentro(this.luzesCentro)
                .observacoes(this.observacoes)
                .build();
    }
}
