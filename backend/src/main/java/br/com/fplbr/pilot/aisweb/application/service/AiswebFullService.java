package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.domain.enums.AerodromoIcaoIataEnum;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service para buscar dados completos do AISWEB usando nossas APIs internas.
 */
@ApplicationScoped
public class AiswebFullService {
    private static final Logger LOG = Logger.getLogger(AiswebFullService.class);

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    String serverPort;

    @Inject
    MeteoDecoderService meteoDecoderService;

    // Thread pool para execução paralela
    private final ExecutorService executor = Executors.newFixedThreadPool(6);
    private final Client httpClient = ClientBuilder.newClient();

    /**
     * Busca o código IATA do aeródromo no enum
     * 
     * @param icao Código ICAO do aeródromo
     * @return Código IATA ou null se não encontrado
     */
    private String buscarIataDoEnum(String icao) {
        try {
            LOG.infof("🔍 [AISWEB-FULL] Buscando IATA para ICAO: %s no enum", icao);
            
            // Buscar IATA no enum
            AerodromoIcaoIataEnum aerodromoEnum = AerodromoIcaoIataEnum.findByIcao(icao);
            
            if (aerodromoEnum != null) {
                String iata = aerodromoEnum.getIata();
                LOG.infof("✅ [AISWEB-FULL] IATA encontrado: %s para ICAO: %s", iata, icao);
                return iata;
            } else {
                LOG.warnf("⚠️ [AISWEB-FULL] IATA não encontrado no enum para ICAO: %s", icao);
                return null;
            }
            
        } catch (Exception e) {
            LOG.errorf("❌ [AISWEB-FULL] Erro ao buscar IATA no enum para ICAO %s: %s", icao, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca dados completos do AISWEB para um aeródromo específico em paralelo.
     * 
     * @param icao Código ICAO do aeródromo
     * @return Map com todos os dados do aeródromo
     */
    public Map<String, Object> buscarDadosCompletos(String icao) {
        try {
            LOG.infof("🚀 [AISWEB-FULL] Iniciando busca completa para ICAO: %s", icao);
            
            // 🔍 PRIMEIRO: Buscar dados do aeródromo no enum (incluindo IATA)
            LOG.infof("🔍 [AISWEB-FULL] Buscando dados do aeródromo no enum...");
            String iata = buscarIataDoEnum(icao);
            LOG.infof("📋 [AISWEB-FULL] IATA encontrado: %s", iata != null ? iata : "NÃO ENCONTRADO");
            
            // Executar todas as chamadas em paralelo usando nossas APIs internas
            LOG.infof("⚡ [AISWEB-FULL] Iniciando chamadas paralelas...");
            CompletableFuture<RotaerDto> rotaerFuture = CompletableFuture.supplyAsync(() -> buscarRotaer(icao), executor);
            CompletableFuture<InfotempDto> infotempFuture = CompletableFuture.supplyAsync(() -> buscarInfotemp(icao), executor);
            CompletableFuture<Map<String, Object>> meteoFuture = CompletableFuture.supplyAsync(() -> buscarMeteo(icao), executor);
            CompletableFuture<CartasDto> cartasFuture = CompletableFuture.supplyAsync(() -> buscarCartas(icao), executor);
            CompletableFuture<NotamDto> notamFuture = CompletableFuture.supplyAsync(() -> buscarNotam(icao), executor);
            CompletableFuture<SunDto> sunFuture = CompletableFuture.supplyAsync(() -> buscarSun(icao), executor);
            
            // Aguardar todos os resultados
            LOG.infof("📊 [AISWEB-FULL] Coletando resultados...");
            Map<String, Object> result = new HashMap<>();
            
            // 🔍 Adicionar dados do aeródromo (incluindo IATA)
            result.put("aerodromo", Map.of(
                "icao", icao,
                "iata", iata != null ? iata : "N/A",
                "fonte", "enum"
            ));
            
            // Coletar resultados das chamadas paralelas
            try {
                RotaerDto rotaer = rotaerFuture.get();
                result.put("rotaer", rotaer);
                LOG.infof("✅ [AISWEB-FULL] ROTAER coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar ROTAER: %s", e.getMessage());
                result.put("rotaer", null);
            }
            
            try {
                InfotempDto infotemp = infotempFuture.get();
                result.put("infotemp", infotemp);
                LOG.infof("✅ [AISWEB-FULL] INFOTEMP coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar INFOTEMP: %s", e.getMessage());
                result.put("infotemp", null);
            }
            
            try {
                Map<String, Object> meteo = meteoFuture.get();
                result.put("meteo", meteo); // Estrutura correta para o frontend
                LOG.infof("✅ [AISWEB-FULL] METEO coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar METEO: %s", e.getMessage());
                result.put("meteo", null);
            }
            
            try {
                CartasDto cartas = cartasFuture.get();
                result.put("cartas", cartas);
                LOG.infof("✅ [AISWEB-FULL] CARTAS coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar CARTAS: %s", e.getMessage());
                result.put("cartas", null);
            }
            
            try {
                NotamDto notam = notamFuture.get();
                result.put("notam", notam);
                LOG.infof("✅ [AISWEB-FULL] NOTAM coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar NOTAM: %s", e.getMessage());
                result.put("notam", null);
            }
            
            try {
                SunDto sun = sunFuture.get();
                result.put("sun", sun);
                LOG.infof("✅ [AISWEB-FULL] SUN coletado");
            } catch (Exception e) {
                LOG.errorf("❌ [AISWEB-FULL] Erro ao coletar SUN: %s", e.getMessage());
                result.put("sun", null);
            }
            
            LOG.infof("🎉 [AISWEB-FULL] Busca completa finalizada para ICAO: %s", icao);
            return result;

        } catch (Exception e) {
            LOG.errorf("💥 [AISWEB-FULL] Erro crítico na busca completa para ICAO %s: %s", icao, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar dados completos do AISWEB: " + e.getMessage(), e);
        }
    }

    private RotaerDto buscarRotaer(String icao) {
        try {
            LOG.infof("🔍 [ROTAER] Buscando ROTAER para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/rotaer/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                RotaerDto rotaer = response.readEntity(RotaerDto.class);
                LOG.infof("✅ [ROTAER] Dados obtidos para: %s", icao);
                return rotaer;
            } else {
                LOG.warnf("⚠️ [ROTAER] Status %d para: %s", response.getStatus(), icao);
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("❌ [ROTAER] Erro para %s: %s", icao, e.getMessage());
            return null;
        }
    }

    private InfotempDto buscarInfotemp(String icao) {
        try {
            LOG.infof("🔍 [INFOTEMP] Buscando INFOTEMP para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/infotemp/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                InfotempDto infotemp = response.readEntity(InfotempDto.class);
                LOG.infof("✅ [INFOTEMP] Dados obtidos para: %s", icao);
                return infotemp;
            } else {
                LOG.warnf("⚠️ [INFOTEMP] Status %d para: %s", response.getStatus(), icao);
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("❌ [INFOTEMP] Erro para %s: %s", icao, e.getMessage());
            return null;
        }
    }

    private Map<String, Object> buscarMeteo(String icao) {
        Map<String, Object> meteoData = new HashMap<>();
        
        try {
            LOG.infof("🔍 [METEO] Buscando METEO para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/meteo/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> meteoResponse = response.readEntity(Map.class);
                
                // Extrair METAR e TAF da resposta
                Object metarObj = meteoResponse.get("metar");
                Object tafObj = meteoResponse.get("taf");
                
                meteoData.put("metar", metarObj);
                meteoData.put("taf", tafObj);
                
                // Decodificar METAR se existir
                if (metarObj != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> metarMap = (Map<String, Object>) metarObj;
                    String metarRaw = (String) metarMap.get("raw");
                    if (metarRaw != null && !metarRaw.trim().isEmpty()) {
                        String metarDecoded = meteoDecoderService.decodeMetar(metarRaw);
                        meteoData.put("metarDecoded", metarDecoded);
                    }
                }
                
                // Decodificar TAF se existir
                if (tafObj != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tafMap = (Map<String, Object>) tafObj;
                    String tafRaw = (String) tafMap.get("raw");
                    if (tafRaw != null && !tafRaw.trim().isEmpty()) {
                        String tafDecoded = meteoDecoderService.decodeTaf(tafRaw);
                        meteoData.put("tafDecoded", tafDecoded);
                    }
                }
                
                LOG.infof("✅ [METEO] Dados obtidos para: %s", icao);
            } else {
                LOG.warnf("⚠️ [METEO] Status %d para: %s", response.getStatus(), icao);
                meteoData.put("metar", null);
                meteoData.put("taf", null);
                meteoData.put("metarDecoded", null);
                meteoData.put("tafDecoded", null);
            }
        } catch (Exception e) {
            LOG.errorf("❌ [METEO] Erro para %s: %s", icao, e.getMessage());
            meteoData.put("metar", null);
            meteoData.put("taf", null);
            meteoData.put("metarDecoded", null);
            meteoData.put("tafDecoded", null);
        }
        
        return meteoData;
    }

    private CartasDto buscarCartas(String icao) {
        try {
            LOG.infof("🔍 [CARTAS] Buscando CARTAS para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/cartas/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                CartasDto cartas = response.readEntity(CartasDto.class);
                LOG.infof("✅ [CARTAS] Dados obtidos para: %s", icao);
                return cartas;
            } else {
                LOG.warnf("⚠️ [CARTAS] Status %d para: %s", response.getStatus(), icao);
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("❌ [CARTAS] Erro para %s: %s", icao, e.getMessage());
            return null;
        }
    }

    private NotamDto buscarNotam(String icao) {
        try {
            LOG.infof("🔍 [NOTAM] Buscando NOTAM para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/notam/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                NotamDto notam = response.readEntity(NotamDto.class);
                LOG.infof("✅ [NOTAM] Dados obtidos para: %s", icao);
                return notam;
            } else {
                LOG.warnf("⚠️ [NOTAM] Status %d para: %s", response.getStatus(), icao);
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("❌ [NOTAM] Erro para %s: %s", icao, e.getMessage());
            return null;
        }
    }

    private SunDto buscarSun(String icao) {
        try {
            LOG.infof("🔍 [SUN] Buscando SUN para: %s", icao);
            WebTarget target = httpClient.target("http://localhost:" + serverPort + "/api/aisweb/sun/" + icao);
            Response response = target.request().get();
            
            if (response.getStatus() == 200) {
                SunDto sun = response.readEntity(SunDto.class);
                LOG.infof("✅ [SUN] Dados obtidos para: %s", icao);
                return sun;
            } else {
                LOG.warnf("⚠️ [SUN] Status %d para: %s", response.getStatus(), icao);
                return null;
            }
        } catch (Exception e) {
            LOG.errorf("❌ [SUN] Erro para %s: %s", icao, e.getMessage());
            return null;
        }
    }
}