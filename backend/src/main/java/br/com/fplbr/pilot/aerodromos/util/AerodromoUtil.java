package br.com.fplbr.pilot.aerodromos.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitário para operações relacionadas a aeródromos e rotas
 */
@ApplicationScoped
public class AerodromoUtil {
    
    // Códigos de países para identificação de voos internacionais
    private static final Set<String> PAISES_BRASIL = new HashSet<>(Arrays.asList("SB", "SJ", "SC"));
    private static final Set<String> AERODROMOS_OCEANICOS = new HashSet<>(Arrays.asList("SBBR", "SBGL", "SBGR", "SBRJ"));
    
    /**
     * Verifica se um aeródromo é terminal
     * Em uma implementação real, isso seria verificado em um banco de dados
     */
    public boolean isAerodromoTerminal(String icao) {
        if (icao == null || icao.length() < 2) {
            return false;
        }
        // Lógica simplificada - em produção, verificar em um banco de dados
        // Aeródromos terminais geralmente são grandes aeroportos
        return icao.matches("SB(GR|SP|RJ|GL|PA|VF|JP|SL|TE|AT)");
    }
    
    /**
     * Calcula a distância aproximada entre dois aeródromos em milhas náuticas (NM)
     * Em uma implementação real, usar uma biblioteca de geolocalização
     */
    public double calcularDistanciaAproximada(String icaoOrigem, String icaoDestino) {
        if (icaoOrigem == null || icaoDestino == null || icaoOrigem.length() < 2 || icaoDestino.length() < 2) {
            return 0;
        }
        
        // Valores de exemplo - em produção, usar coordenadas reais
        if ((icaoOrigem.equals("SBSP") && icaoDestino.equals("SBGR")) ||
            (icaoOrigem.equals("SBGR") && icaoDestino.equals("SBSP"))) {
            return 15.0; // Distância aproximada em NM
        }
        
        // Para fins de demonstração, considerar voos longos como > 1000NM
        return 1200.0;
    }
    
    /**
     * Verifica se um voo é internacional com base nos códigos ICAO de partida e destino
     */
    public boolean isVooInternacional(String icaoPartida, String icaoDestino) {
        if (icaoPartida == null || icaoDestino == null || 
            icaoPartida.length() < 2 || icaoDestino.length() < 2) {
            return false;
        }
        
        String paisPartida = icaoPartida.substring(0, 2);
        String paisDestino = icaoDestino.substring(0, 2);
        
        return !paisPartida.equals(paisDestino) || 
               (!PAISES_BRASIL.contains(paisPartida) || !PAISES_BRASIL.contains(paisDestino));
    }
    
    /**
     * Verifica se um voo cruza o oceano com base nos aeródromos de partida e destino
     */
    public boolean isVooSobreOceano(String icaoPartida, String icaoDestino) {
        if (icaoPartida == null || icaoDestino == null) {
            return false;
        }
        
        // Em uma implementação real, isso seria verificado com base nas coordenadas
        // e em um banco de dados de rotas oceânicas
        return AERODROMOS_OCEANICOS.contains(icaoPartida) || 
               AERODROMOS_OCEANICOS.contains(icaoDestino) ||
               calcularDistanciaAproximada(icaoPartida, icaoDestino) > 1000;
    }
}
