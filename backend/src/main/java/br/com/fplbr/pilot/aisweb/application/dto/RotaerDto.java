package br.com.fplbr.pilot.aisweb.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para dados ROTAER de um aeródromo.
 */
public record RotaerDto(
    // Coordenadas
    Double latitude,
    Double longitude,
    String operadora,
    
    // Metadados
    String icao,
    String nome,
    String cidade,
    String uf,
    String iata,
    Integer altitude,
    
    // Novos campos da API AISWEB
    String ciad,
    String typeUtil,
    String typeOpr,
    String utc,
    Integer altFt,
    
    // Frequência de operação
    Boolean frequenciaOperacaoAerodromo,
    
    // Iluminação
    List<IluminacaoAerodromoDto> iluminacaoAerodromo,
    
    // Pistas
    List<PistaDto> pistas,
    
    // Serviços
    ServicosDto servicos,
    
    // Observações
    List<ObservacaoDto> observacoes,
    
    // Distâncias declaradas
    List<DistanciaDeclaradaDto> distanciasDeclaradas,
    
    // Complementos
    List<ComplementoDto> complementos,
    
    // Data de publicação
    LocalDateTime dataPublicacaoRotaer,
    
    // Contadores
    Integer qtdePistas,
    Integer qtdeObservacoes
) {}
