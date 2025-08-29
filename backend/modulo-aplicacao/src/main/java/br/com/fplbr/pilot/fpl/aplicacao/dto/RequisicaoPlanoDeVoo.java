package br.com.fplbr.pilot.fpl.aplicacao.dto;

public class RequisicaoPlanoDeVoo {
    public String identificacaoAeronave;

    public String regrasDeVoo;         // IFR | VFR | IFR_PARA_VFR | VFR_PARA_IFR | I | V | Y | Z
    public String tipoDeVoo;           // REGULAR | NAO_REGULAR | GERAL | MILITAR | OUTROS | S/N/G/M/X

    public Integer quantidadeAeronaves; // 1..99 (default 1)
    public String tipoAeronaveIcao;     // C172, A20N...
    public String esteira;              // SUPER/PESADA/MEDIA/LEVE | J/H/M/L

    public String equipamentos;         // SDFGRY/S etc

    public String aerodromoPartida;     // ICAO
    public String horaPartidaZulu;      // HHMM

    public String velocidadeCruzeiro;   // N0120 | K0750 | M082
    public String nivelCruzeiro;        // F090 | A045 | M090 | S113
    public String rota;                 // rota textual

    public String aerodromoDestino;     // ICAO
    public String eetTotal;             // HHMM
    public String alternativo1;         // opcional
    public String alternativo2;         // opcional

    public String outrosDados;          // DOF/..., RMK/...

    public String autonomia;            // HHMM para E/
    public Integer pessoasABordo;       // para P/
    public String piloto;               // livre
}
