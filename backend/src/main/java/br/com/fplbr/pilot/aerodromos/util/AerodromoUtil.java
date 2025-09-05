package br.com.fplbr.pilot.aerodromos.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * UtilitÃ¡rio para operaÃ§Ãµes relacionadas a aerÃ³dromos e rotas
 */
@ApplicationScoped
public class AerodromoUtil {
    
    // CÃ³digos de paÃ­ses para identificaÃ§Ã£o de voos internacionais
    private static final Set<String> PAISES_BRASIL = new HashSet<>(Arrays.asList("SB", "SJ", "SC"));
    private static final Set<String> AERODROMOS_OCEANICOS = new HashSet<>(Arrays.asList("SBBR", "SBGL", "SBGR", "SBRJ"));
    
    /**
     * Verifica se um aerÃ³dromo Ã© terminal
     * Em uma implementaÃ§Ã£o real, isso seria verificado em um banco de dados
     */
    public boolean isAerodromoTerminal(String icao) {
        if (icao == null || icao.length() < 2) {
            return false;
        }
        // LÃ³gica simplificada - em produÃ§Ã£o, verificar em um banco de dados
        // AerÃ³dromos terminais geralmente sÃ£o grandes aeroportos
        return icao.matches("SB(GR|SP|RJ|GL|PA|VF|JP|SL|TE|AT)");
    }
    
    /**
     * Calcula a distÃ¢ncia aproximada entre dois aerÃ³dromos em milhas nÃ¡uticas (NM)
     * Em uma implementaÃ§Ã£o real, usar uma biblioteca de geolocalizaÃ§Ã£o
     */
    public double calcularDistanciaAproximada(String icaoOrigem, String icaoDestino) {
        if (icaoOrigem == null || icaoDestino == null || icaoOrigem.length() < 2 || icaoDestino.length() < 2) {
            return 0;
        }
        
        // Valores de exemplo - em produÃ§Ã£o, usar coordenadas reais
        if ((icaoOrigem.equals("SBSP") && icaoDestino.equals("SBGR")) ||
            (icaoOrigem.equals("SBGR") && icaoDestino.equals("SBSP"))) {
            return 15.0; // DistÃ¢ncia aproximada em NM
        }
        
        // Para fins de demonstraÃ§Ã£o, considerar voos longos como > 1000NM
        return 1200.0;
    }
    
    /**
     * Verifica se um voo Ã© internacional com base nos cÃ³digos ICAO de partida e destino
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
     * Verifica se um voo cruza o oceano com base nos aerÃ³dromos de partida e destino
     */
    public boolean isVooSobreOceano(String icaoPartida, String icaoDestino) {
        if (icaoPartida == null || icaoDestino == null) {
            return false;
        }
        
        // Em uma implementaÃ§Ã£o real, isso seria verificado com base nas coordenadas
        // e em um banco de dados de rotas oceÃ¢nicas
        return AERODROMOS_OCEANICOS.contains(icaoPartida) || 
               AERODROMOS_OCEANICOS.contains(icaoDestino) ||
               calcularDistanciaAproximada(icaoPartida, icaoDestino) > 1000;
    }
}
