package br.com.fplbr.pilot.aerodromos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AerodromoDTO {
    private String icao;
    private String iata;
    private String nomeOficial;
    private String municipio;
    private String uf;
    private String latitude;
    private String longitude;
    private String altitudePistaPesos;
    private String altitudePistaTemperatura;
    private String comprimentoPistaPrincipal;
    private String larguraPistaPrincipal;
    private String designacaoSuperficiePistaPrincipal;
    private String pcnPistaPrincipal;
    private String resistenciaPistaPrincipal;
    private String pistaPrincipalCabeceiras;
    private String pistaPrincipalElevacao;
    private String pistaPrincipalCoordenadas;
    private String pistaPrincipalDimensoes;
    private String pistaPrincipalSuperficie;
    private String pistaPrincipalPcn;
    private String pistaPrincipalResistencia;
    private String pistaPrincipalLargura;
    private String pistaPrincipalComprimento;
    private String pistaPrincipalDesignacaoSuperficie;
    private String pistaPrincipalResistenciaPavimento;
    private String pistaPrincipalCategoriaResistencia;
    private String pistaPrincipalTipoPavimento;
    private String pistaPrincipalTracadoPista;
    private String pistaPrincipalEstadoPista;
    private String pistaPrincipalCabeceira1;
    private String pistaPrincipalCabeceira2;
    private String pistaPrincipalElevacao1;
    private String pistaPrincipalElevacao2;
    private String pistaPrincipalCoordenadas1;
    private String pistaPrincipalCoordenadas2;
    private String pistaPrincipalDimensoes1;
    private String pistaPrincipalDimensoes2;
    private String pistaPrincipalSuperficie1;
    private String pistaPrincipalSuperficie2;
    private String pistaPrincipalPcn1;
    private String pistaPrincipalPcn2;
    private String pistaPrincipalResistencia1;
    private String pistaPrincipalResistencia2;
    private String pistaPrincipalLargura1;
    private String pistaPrincipalLargura2;
    private String pistaPrincipalComprimento1;
    private String pistaPrincipalComprimento2;
    private String pistaPrincipalDesignacaoSuperficie1;
    private String pistaPrincipalDesignacaoSuperficie2;
    private String pistaPrincipalResistenciaPavimento1;
    private String pistaPrincipalResistenciaPavimento2;
    private String pistaPrincipalCategoriaResistencia1;
    private String pistaPrincipalCategoriaResistencia2;
    private String pistaPrincipalTipoPavimento1;
    private String pistaPrincipalTipoPavimento2;
    private String pistaPrincipalTracadoPista1;
    private String pistaPrincipalTracadoPista2;
    private String pistaPrincipalEstadoPista1;
    private String pistaPrincipalEstadoPista2;
    
    // New fields for charts and sun info
    private List<CartaAerodromoDTO> cartas = new ArrayList<>();
    private String nascerSol;
    private String porDoSol;
}
