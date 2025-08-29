package br.com.fplbr.pilot.fpl.dominio.modelo;

public class PlanoDeVoo {

    // Item 7
    private final IdentificacaoAeronave identificacaoAeronave;

    // Item 8
    private final RegrasDeVoo regrasDeVoo;
    private final TipoDeVoo tipoDeVoo;

    // Item 9
    private final Integer quantidadeAeronaves; // 1..99
    private final String tipoAeronaveIcao;     // ex: C172, A20N
    private final EsteiraDeTurbulencia esteira;

    // Item 10
    private final String equipamentos; // ex: SDFGRY/S

    // Item 13
    private final String aerodromoPartida;    // ICAO 4 letras
    private final String horaPartidaZulu;     // HHMM UTC

    // Item 15
    private final String velocidadeCruzeiro;  // N0120 | K0750 | M082
    private final String nivelCruzeiro;       // F090 | A045 | M090 | S113
    private final String rota;                // string da rota

    // Item 16
    private final String aerodromoDestino;    // ICAO
    private final String eetTotal;            // HHMM
    private final String alternativo1;        // opcional
    private final String alternativo2;        // opcional

    // Item 18
    private final String outrosDados;         // ex: DOF/yyyymmdd RMK/...

    // Item 19 (mínimo para pré-visualização)
    private final String autonomia;           // HHMM → E/****
    private final Integer pessoasABordo;      // → P/nnn
    private final String piloto;              // livre

    public PlanoDeVoo(IdentificacaoAeronave identificacaoAeronave,
                      RegrasDeVoo regrasDeVoo,
                      TipoDeVoo tipoDeVoo,
                      Integer quantidadeAeronaves,
                      String tipoAeronaveIcao,
                      EsteiraDeTurbulencia esteira,
                      String equipamentos,
                      String aerodromoPartida,
                      String horaPartidaZulu,
                      String velocidadeCruzeiro,
                      String nivelCruzeiro,
                      String rota,
                      String aerodromoDestino,
                      String eetTotal,
                      String alternativo1,
                      String alternativo2,
                      String outrosDados,
                      String autonomia,
                      Integer pessoasABordo,
                      String piloto) {

        this.identificacaoAeronave = identificacaoAeronave;
        this.regrasDeVoo = regrasDeVoo;
        this.tipoDeVoo = tipoDeVoo;
        this.quantidadeAeronaves = quantidadeAeronaves;
        this.tipoAeronaveIcao = tipoAeronaveIcao;
        this.esteira = esteira;
        this.equipamentos = equipamentos;
        this.aerodromoPartida = aerodromoPartida;
        this.horaPartidaZulu = horaPartidaZulu;
        this.velocidadeCruzeiro = velocidadeCruzeiro;
        this.nivelCruzeiro = nivelCruzeiro;
        this.rota = rota;
        this.aerodromoDestino = aerodromoDestino;
        this.eetTotal = eetTotal;
        this.alternativo1 = alternativo1;
        this.alternativo2 = alternativo2;
        this.outrosDados = outrosDados;
        this.autonomia = autonomia;
        this.pessoasABordo = pessoasABordo;
        this.piloto = piloto;
    }

    public IdentificacaoAeronave getIdentificacaoAeronave() { return identificacaoAeronave; }
    public RegrasDeVoo getRegrasDeVoo() { return regrasDeVoo; }
    public TipoDeVoo getTipoDeVoo() { return tipoDeVoo; }
    public Integer getQuantidadeAeronaves() { return quantidadeAeronaves; }
    public String getTipoAeronaveIcao() { return tipoAeronaveIcao; }
    public EsteiraDeTurbulencia getEsteira() { return esteira; }
    public String getEquipamentos() { return equipamentos; }
    public String getAerodromoPartida() { return aerodromoPartida; }
    public String getHoraPartidaZulu() { return horaPartidaZulu; }
    public String getVelocidadeCruzeiro() { return velocidadeCruzeiro; }
    public String getNivelCruzeiro() { return nivelCruzeiro; }
    public String getRota() { return rota; }
    public String getAerodromoDestino() { return aerodromoDestino; }
    public String getEetTotal() { return eetTotal; }
    public String getAlternativo1() { return alternativo1; }
    public String getAlternativo2() { return alternativo2; }
    public String getOutrosDados() { return outrosDados; }
    public String getAutonomia() { return autonomia; }
    public Integer getPessoasABordo() { return pessoasABordo; }
    public String getPiloto() { return piloto; }
}

