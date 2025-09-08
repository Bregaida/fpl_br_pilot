package br.com.fplbr.pilot.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanoDeVooDTO {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{3,7}$")
    private String identificacaoDaAeronave;

    @NotNull
    private Boolean indicativoDeChamada;

    @NotBlank
    @Pattern(regexp = "^[IVYZ]$")
    private String regraDeVooEnum;

    @NotBlank
    @Pattern(regexp = "^[GSNMX]$")
    private String tipoDeVooEnum;

    @NotNull
    @Min(1)
    private Integer numeroDeAeronaves;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{2,4}$")
    private String tipoDeAeronave;

    @NotBlank
    @Pattern(regexp = "^[LMHJ]$")
    private String categoriaEsteiraTurbulenciaEnum;

    @NotNull
    @Size(min = 1)
    private List<@NotBlank String> equipamentoCapacidadeDaAeronave;

    @NotNull
    @Size(min = 1)
    private List<@NotBlank String> vigilancia;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDePartida;

    @NotNull
    private OffsetDateTime horaPartida; // ISO UTC esperado

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDeDestino;

    @NotBlank
    @Pattern(regexp = "^([0-1]\\d|2[0-3])[0-5]\\d$")
    private String tempoDeVooPrevisto; // HHmm

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$")
    private String aerodromoDeAlternativa;

    @Pattern(regexp = "^$|^[A-Z]{4}$")
    private String aerodromoDeAlternativaSegundo; // opcional

    @NotBlank
    @Pattern(regexp = "^[NKM][0-9]{4}$")
    private String velocidadeDeCruzeiro;

    @NotBlank
    @Pattern(regexp = "^(VFR|[AF][0-9]{3})$")
    private String nivelDeVoo;

    @NotBlank
    private String rota;

    @Valid
    @NotNull
    private OutrasInformacoes outrasInformacoes;

    @Valid
    @NotNull
    private InformacaoSuplementar informacaoSuplementar;

    @NotBlank
    @Pattern(regexp = "^(PVC|PVS)$")
    private String modo; // PVC | PVS

    // ==== Tipos aninhados ====

    @Getter
    @Setter
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutrasInformacoes {
        @Pattern(regexp = "^$|^(?:[A-Z]{3,4}[0-9]{4})(?:\\s+[A-Z]{3,4}[0-9]{4})*$")
        private String eet; // SBC0005 SBBS0150 ...

        @NotBlank
        private String opr;

        @NotBlank
        @Pattern(regexp = "^[A-Z]{4}$")
        private String from;

        private List<@Pattern(regexp = "^[A-Z]$") String> per; // PVC

        private String rmk; // deve conter REA quando rota tiver REA

        @NotBlank
        @Pattern(regexp = "^\\d{6}$")
        private String dof; // ddmmaa
    }

    @Getter
    @Setter
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BotesInfo {
        @NotNull
        private Boolean possui;
        @Min(1)
        private Integer numero;
        @Min(1)
        private Integer capacidade;
        private Boolean c; // abrigo
        private String cor;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformacaoSuplementar {
        @NotBlank
        @Pattern(regexp = "^([0-1]\\d|2[0-3])[0-5]\\d$")
        private String autonomia; // HHmm

        private List<@Pattern(regexp = "^[UVE]$") String> radioEmergencia;
        private List<@Pattern(regexp = "^[SPDMJ]$") String> sobrevivencia;
        private List<@Pattern(regexp = "^[JLFUV]$") String> coletes;

        @Valid
        private BotesInfo botes;

        @NotBlank
        private String corEMarcaAeronave;

        private String observacoes;

        @NotBlank
        private String pilotoEmComando;

        @NotBlank
        private String anacPrimeiroPiloto;

        private String anacSegundoPiloto;

        @NotBlank
        @Pattern(regexp = "^\\d{10,11}$")
        private String telefone;

        private Boolean n; // se true, observações obrigatório
    }
}


