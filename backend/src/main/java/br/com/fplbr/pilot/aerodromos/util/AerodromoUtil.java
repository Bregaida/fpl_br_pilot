package br.com.fplbr.pilot.aerodromos.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * UtilitÃƒÂ¡rio para operaÃƒÂ§ÃƒÂµes relacionadas a aerÃƒÂ³dromos e rotas
 */
@ApplicationScoped
public class AerodromoUtil {
    
    // CÃƒÂ³digos de paÃƒÂ­ses para identificaÃƒÂ§ÃƒÂ£o de voos internacionais
    private static final Set<String> PAISES_BRASIL = new HashSet<>(Arrays.asList("SB", "SJ", "SC"));
    private static final Set<String> AERODROMOS_OCEANICOS = new HashSet<>(Arrays.asList("SBBR", "SBGL", "SBGR", "SBRJ"));
    
    /**
     * Verifica se um aerÃƒÂ³dromo ÃƒÂ© terminal
     * Em uma implementaÃƒÂ§ÃƒÂ£o real, isso seria verificado em um banco de dados
     */
    public boolean isAerodromoTerminal(String icao) {
        if (icao == null || icao.length() < 2) {
            return false;
        }
        // LÃƒÂ³gica simplificada - em produÃƒÂ§ÃƒÂ£o, verificar em um banco de dados
        // AerÃƒÂ³dromos terminais geralmente sÃƒÂ£o grandes aeroportos
        return icao.matches("SB(GR|SP|RJ|GL|PA|VF|JP|SL|TE|AT)");
    }
    
    /**
     * Calcula a distÃƒÂ¢ncia aproximada entre dois aerÃƒÂ³dromos em milhas nÃƒÂ¡uticas (NM)
     * Em uma implementaÃƒÂ§ÃƒÂ£o real, usar uma biblioteca de geolocalizaÃƒÂ§ÃƒÂ£o
     */
    public double calcularDistanciaAproximada(String icaoOrigem, String icaoDestino) {
        if (icaoOrigem == null || icaoDestino == null || icaoOrigem.length() < 2 || icaoDestino.length() < 2) {
            return 0;
        }
        
        // Valores de exemplo - em produÃƒÂ§ÃƒÂ£o, usar coordenadas reais
        if ((icaoOrigem.equals("SBSP") && icaoDestino.equals("SBGR")) ||
            (icaoOrigem.equals("SBGR") && icaoDestino.equals("SBSP"))) {
            return 15.0; // DistÃƒÂ¢ncia aproximada em NM
        }
        
        // Para fins de demonstraÃƒÂ§ÃƒÂ£o, considerar voos longos como > 1000NM
        return 1200.0;
    }
    
    /**
     * Verifica se um voo ÃƒÂ© internacional com base nos cÃƒÂ³digos ICAO de partida e destino
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
     * Verifica se um voo cruza o oceano com base nos aerÃƒÂ³dromos de partida e destino
     */
    public boolean isVooSobreOceano(String icaoPartida, String icaoDestino) {
        if (icaoPartida == null || icaoDestino == null) {
            return false;
        }
        
        // Em uma implementaÃƒÂ§ÃƒÂ£o real, isso seria verificado com base nas coordenadas
        // e em um banco de dados de rotas oceÃƒÂ¢nicas
        return AERODROMOS_OCEANICOS.contains(icaoPartida) || 
               AERODROMOS_OCEANICOS.contains(icaoDestino) ||
               calcularDistanciaAproximada(icaoPartida, icaoDestino) > 1000;
    }
}
