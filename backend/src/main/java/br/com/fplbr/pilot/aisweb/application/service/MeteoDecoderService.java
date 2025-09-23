package br.com.fplbr.pilot.aisweb.application.service;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.*;

/**
 * Serviço para decodificação de METAR e TAF
 */
@ApplicationScoped
public class MeteoDecoderService {
    
    private static final Logger LOG = Logger.getLogger(MeteoDecoderService.class);
    
    // Padrões para parsing (removidos por não serem utilizados na implementação atual)
    
    // Dicionários de tradução
    private static final Map<String, String> WEATHER_CODES = new HashMap<>();
    private static final Map<String, String> CLOUD_CODES = new HashMap<>();
    private static final Map<String, String> INTENSITY_CODES = new HashMap<>();
    
    static {
        // Códigos de tempo
        WEATHER_CODES.put("BR", "Nevoeiro");
        WEATHER_CODES.put("FG", "Nevoeiro denso");
        WEATHER_CODES.put("FU", "Fumaça");
        WEATHER_CODES.put("HZ", "Névoa");
        WEATHER_CODES.put("DU", "Poeira generalizada");
        WEATHER_CODES.put("SA", "Areia");
        WEATHER_CODES.put("DS", "Tempestade de poeira");
        WEATHER_CODES.put("SS", "Tempestade de areia");
        WEATHER_CODES.put("PO", "Redemoinhos de areia");
        WEATHER_CODES.put("SQ", "Squalls");
        WEATHER_CODES.put("FC", "Nuvem de funil");
        WEATHER_CODES.put("TS", "Tempestade");
        WEATHER_CODES.put("SH", "Chuva");
        WEATHER_CODES.put("RA", "Chuva");
        WEATHER_CODES.put("DZ", "Garoa");
        WEATHER_CODES.put("SN", "Neve");
        WEATHER_CODES.put("SG", "Grãos de neve");
        WEATHER_CODES.put("IC", "Cristais de gelo");
        WEATHER_CODES.put("PL", "Granulados de gelo");
        WEATHER_CODES.put("GR", "Granizo");
        WEATHER_CODES.put("GS", "Pequeno granizo");
        WEATHER_CODES.put("UP", "Precipitação desconhecida");
        WEATHER_CODES.put("VA", "Cinzas vulcânicas");
        
        // Códigos combinados
        WEATHER_CODES.put("TSRA", "Tempestade com chuva");
        WEATHER_CODES.put("TSGR", "Tempestade com granizo");
        WEATHER_CODES.put("TSSN", "Tempestade com neve");
        WEATHER_CODES.put("SHRA", "Chuva de pancadas");
        WEATHER_CODES.put("SHSN", "Neve de pancadas");
        WEATHER_CODES.put("SHGR", "Granizo de pancadas");
        
        // Códigos de nuvens
        CLOUD_CODES.put("FEW", "Poucas nuvens");
        CLOUD_CODES.put("SCT", "Nuvens esparsas");
        CLOUD_CODES.put("BKN", "Céu nublado");
        CLOUD_CODES.put("OVC", "Encoberto");
        CLOUD_CODES.put("SKC", "Céu claro");
        CLOUD_CODES.put("CLR", "Céu claro");
        CLOUD_CODES.put("NSC", "Sem cobertura significativa");
        CLOUD_CODES.put("NCD", "Nenhuma nuvem detectada");
        CLOUD_CODES.put("CB", "Cumulonimbus");
        CLOUD_CODES.put("TCU", "Cumulus torrejante");
        
        // Códigos de intensidade
        INTENSITY_CODES.put("-", "Leve");
        INTENSITY_CODES.put("+", "Forte");
        INTENSITY_CODES.put("RE", "Recente");
        INTENSITY_CODES.put("VC", "Nas proximidades");
        INTENSITY_CODES.put("BC", "Partes de");
        INTENSITY_CODES.put("DR", "Deriva baixa");
        INTENSITY_CODES.put("MI", "Raso");
        INTENSITY_CODES.put("PR", "Parcial");
        INTENSITY_CODES.put("BL", "Soprando");
        INTENSITY_CODES.put("FZ", "Congelamento");
    }
    
    public String decodeMetar(String rawMetar) {
        if (rawMetar == null || rawMetar.trim().isEmpty()) {
            return "METAR não disponível";
        }
        
        try {
            LOG.infof("🔍 [METEO-DECODER] Iniciando decodificação do METAR: %s", rawMetar);
            StringBuilder decoded = new StringBuilder();
            String[] parts = rawMetar.trim().split("\\s+");
            LOG.infof("🔍 [METEO-DECODER] Partes do METAR: %s", String.join(" | ", parts));
            
            int index = 0;
            
            // 1. Tipo de observação
            if (index < parts.length) {
                String type = parts[index++];
                if ("METAR".equals(type)) {
                    decoded.append("📊 Observação meteorológica regular\n");
                } else if ("SPECI".equals(type)) {
                    decoded.append("📊 Observação meteorológica especial\n");
                }
            }
            
            // 2. Código ICAO
            if (index < parts.length) {
                String icao = parts[index++];
                decoded.append("🏢 Aeródromo: ").append(icao).append("\n");
            }
            
            // 3. Data e hora
            if (index < parts.length) {
                String dateTime = parts[index++];
                if (dateTime.matches("\\d{6}Z")) {
                    String day = dateTime.substring(0, 2);
                    String hour = dateTime.substring(2, 4);
                    String minute = dateTime.substring(4, 6);
                    decoded.append("📅 Dia: ").append(day).append(" às ").append(hour).append(":").append(minute).append("Z\n");
                }
            }
            
            // 4. Modificadores
            if (index < parts.length) {
                String modifier = parts[index];
                if ("AUTO".equals(modifier)) {
                    decoded.append("🤖 Observação automática\n");
                    index++;
                } else if ("COR".equals(modifier)) {
                    decoded.append("✏️ Correção\n");
                    index++;
                } else if ("NIL".equals(modifier)) {
                    decoded.append("❌ Dados não disponíveis\n");
                    index++;
                }
            }
            
            // 5. Vento
            if (index < parts.length) {
                String wind = parts[index];
                if (wind.matches("\\d{3}\\d{2}G?\\d{0,2}KT")) {
                    // Verificar se a próxima parte é uma variação de vento
                    String windVariation = "";
                    if (index + 1 < parts.length && parts[index + 1].matches("\\d{3}V\\d{3}")) {
                        windVariation = parts[index + 1];
                        index++; // Pular a variação
                    }
                    decoded.append(decodeWind(wind + " " + windVariation)).append("\n");
                    index++;
                } else if ("00000KT".equals(wind)) {
                    decoded.append("💨 Vento: Calmo\n");
                    index++;
                } else if (wind.startsWith("VRB")) {
                    String speed = wind.substring(3, 5);
                    decoded.append("💨 Vento: Variável ").append(speed).append(" nós\n");
                    index++;
                } else if ("/////KT".equals(wind)) {
                    decoded.append("💨 Vento: Não disponível\n");
                    index++;
                }
            }
            
            // 6. Visibilidade
            if (index < parts.length) {
                String visibility = parts[index];
                if (visibility.matches("\\d{4}") || visibility.matches("\\d{2}SM") || "CAVOK".equals(visibility) || "////".equals(visibility)) {
                    decoded.append(decodeVisibility(visibility)).append("\n");
                    index++;
                }
            }
            
            // 7. Tempo presente
            boolean hasWeather = false;
            while (index < parts.length) {
                String part = parts[index];
                if (part.matches("[+-]?[A-Z]{2,3}")) {
                    if (!hasWeather) {
                        decoded.append("🌦️ Tempo presente: ");
                        hasWeather = true;
                    }
                    decoded.append(decodeWeather(part)).append(" ");
                    index++;
                } else {
                    break;
                }
            }
            if (hasWeather) {
                decoded.append("\n");
            }
            
            // 8. Nuvens
            while (index < parts.length) {
                String part = parts[index];
                if (part.matches("(FEW|SCT|BKN|OVC|SKC|CLR|NSC|NCD)\\d{0,3}(CB|TCU)?")) {
                    decoded.append(decodeCloud(part)).append("\n");
                    index++;
                } else {
                    break;
                }
            }
            
            // 9. Temperatura e ponto de orvalho
            if (index < parts.length) {
                String temp = parts[index];
                if (temp.matches("\\d{2}/\\d{2}|M\\d{2}/M\\d{2}|\\d{2}/M\\d{2}|M\\d{2}/\\d{2}")) {
                    decoded.append(decodeTemperature(temp)).append("\n");
                    index++;
                }
            }
            
            // 10. Pressão
            if (index < parts.length) {
                String pressure = parts[index];
                if (pressure.matches("[QA]\\d{4}")) {
                    decoded.append(decodePressure(pressure)).append("\n");
                    index++;
                }
            }
            
            // 11. Tendências e observações
            if (index < parts.length) {
                decoded.append("📝 Tendências e observações:\n");
                for (int i = index; i < parts.length; i++) {
                    decoded.append("   ").append(parts[i]).append(" ");
                }
                decoded.append("\n");
            }
            
            String result = decoded.toString();
            LOG.infof("🔍 [METEO-DECODER] Resultado da decodificação: %s", result);
            return result;
            
        } catch (Exception e) {
            LOG.error("Erro ao decodificar METAR: " + rawMetar, e);
            return "Erro na decodificação do METAR";
        }
    }
    
    public String decodeTaf(String rawTaf) {
        if (rawTaf == null || rawTaf.trim().isEmpty()) {
            return "TAF não disponível";
        }
        
        try {
            StringBuilder decoded = new StringBuilder();
            String[] parts = rawTaf.trim().split("\\s+");
            
            int index = 0;
            
            // 1. TAF
            if (index < parts.length && "TAF".equals(parts[index])) {
                decoded.append("📈 Previsão Terminal de Aeródromo\n");
                index++;
            }
            
            // 2. Código ICAO
            if (index < parts.length) {
                String icao = parts[index++];
                decoded.append("🏢 Aeródromo: ").append(icao).append("\n");
            }
            
            // 3. Data e hora de confecção
            if (index < parts.length) {
                String dateTime = parts[index++];
                if (dateTime.matches("\\d{6}Z")) {
                    String day = dateTime.substring(0, 2);
                    String hour = dateTime.substring(2, 4);
                    String minute = dateTime.substring(4, 6);
                    decoded.append("📅 Confecção: Dia ").append(day).append(" às ").append(hour).append(":").append(minute).append("Z\n");
                }
            }
            
            // 4. Período de validade
            if (index < parts.length) {
                String validity = parts[index++];
                if (validity.matches("\\d{4}/\\d{4}")) {
                    String startDay = validity.substring(0, 2);
                    String startHour = validity.substring(2, 4);
                    String endDay = validity.substring(5, 7);
                    String endHour = validity.substring(7, 9);
                    decoded.append("⏰ Válido: Dia ").append(startDay).append(" ").append(startHour).append("Z até Dia ").append(endDay).append(" ").append(endHour).append("Z\n");
                }
            }
            
            // 5. Vento
            if (index < parts.length) {
                String wind = parts[index];
                if (wind.matches("\\d{3}\\d{2}G?\\d{0,2}KT")) {
                    decoded.append(decodeWind(wind)).append("\n");
                    index++;
                } else if ("00000KT".equals(wind)) {
                    decoded.append("💨 Vento: Calmo\n");
                    index++;
                } else if (wind.startsWith("VRB")) {
                    String speed = wind.substring(3, 5);
                    decoded.append("💨 Vento: Variável ").append(speed).append(" nós\n");
                    index++;
                }
            }
            
            // 6. Visibilidade
            if (index < parts.length) {
                String visibility = parts[index];
                if (visibility.matches("\\d{4}") || visibility.matches("\\d{2}SM") || "CAVOK".equals(visibility) || "////".equals(visibility)) {
                    decoded.append(decodeVisibility(visibility)).append("\n");
                    index++;
                }
            }
            
            // 7. Tempo presente
            while (index < parts.length) {
                String part = parts[index];
                if (part.matches("[+-]?[A-Z]{2,3}")) {
                    decoded.append(decodeWeather(part)).append(" ");
                    index++;
                } else {
                    break;
                }
            }
            if (decoded.toString().contains("Tempo presente:")) {
                decoded.append("\n");
            }
            
            // 8. Nuvens
            while (index < parts.length) {
                String part = parts[index];
                if (part.matches("(FEW|SCT|BKN|OVC|SKC|CLR|NSC|NCD)\\d{0,3}(CB|TCU)?")) {
                    decoded.append(decodeCloud(part)).append("\n");
                    index++;
                } else {
                    break;
                }
            }
            
            // 9. Temperaturas máximas e mínimas
            while (index < parts.length) {
                String part = parts[index];
                if (part.startsWith("TX") || part.startsWith("TN")) {
                    decoded.append(decodeTemperatureForecast(part)).append("\n");
                    index++;
                } else {
                    break;
                }
            }
            
            // 10. Tendências (BECMG, TEMPO, PROB) - processar todas as tendências
            while (index < parts.length) {
                String part = parts[index];
                if (part.startsWith("BECMG") || part.startsWith("TEMPO") || part.startsWith("PROB") || 
                    part.startsWith("FM") || part.startsWith("TL") || part.startsWith("AT")) {
                    decoded.append(decodeTrend(parts, index)).append("\n");
                    // Processar condições meteorológicas após a tendência
                    index = processTrendConditions(parts, index + 1, decoded);
                } else if (part.equals("RMK")) {
                    // Processar observações
                    decoded.append("📝 Observações: ");
                    for (int i = index + 1; i < parts.length; i++) {
                        decoded.append(parts[i]).append(" ");
                    }
                    decoded.append("\n");
                    break;
                } else {
                    index++;
                }
            }
            
            return decoded.toString();
            
        } catch (Exception e) {
            LOG.error("Erro ao decodificar TAF: " + rawTaf, e);
            return "Erro na decodificação do TAF";
        }
    }
    
    private String decodeWind(String wind) {
        if (wind.length() < 7) return "💨 Vento: Dados incompletos";
        
        // Separar vento principal da variação se houver
        String[] windParts = wind.split("\\s+");
        String mainWind = windParts[0];
        String variation = windParts.length > 1 ? windParts[1] : "";
        
        String direction = mainWind.substring(0, 3);
        String speed = mainWind.substring(3, 5);
        String gust = "";
        
        if (mainWind.contains("G")) {
            int gIndex = mainWind.indexOf("G");
            if (gIndex + 2 < mainWind.length()) {
                gust = mainWind.substring(gIndex + 1, gIndex + 3);
            }
        }
        
        StringBuilder result = new StringBuilder("💨 Vento: ");
        
        if ("000".equals(direction)) {
            result.append("Calmo");
        } else if ("VRB".equals(direction)) {
            result.append("Variável");
        } else {
            result.append(direction).append("°");
        }
        
        result.append(" ").append(speed).append(" nós");
        
        if (!gust.isEmpty()) {
            result.append(" com rajadas de ").append(gust).append(" nós");
        }
        
        if (!variation.isEmpty() && variation.length() >= 6) {
            result.append(" variável de ").append(variation.substring(0, 3)).append("° a ").append(variation.substring(4, 7)).append("°");
        }
        
        return result.toString();
    }
    
    private String decodeVisibility(String visibility) {
        if ("CAVOK".equals(visibility)) {
            return "☀️ Teto e visibilidade Ok: CAVOK";
        }
        
        if ("////".equals(visibility)) {
            return "👁️ Visibilidade: Não disponível";
        }
        
        if (visibility.endsWith("SM")) {
            String miles = visibility.substring(0, visibility.length() - 2);
            return "👁️ Visibilidade: " + miles + " milhas";
        }
        
        if (visibility.matches("\\d{4}")) {
            int meters = Integer.parseInt(visibility);
            if (meters >= 10000) {
                return "👁️ Visibilidade: > 10 km";
            } else if (meters >= 1000) {
                return "👁️ Visibilidade: " + (meters / 1000) + " km";
            } else {
                return "👁️ Visibilidade: " + meters + " metros";
            }
        }
        
        return "👁️ Visibilidade: " + visibility;
    }
    
    private String decodeWeather(String weather) {
        StringBuilder result = new StringBuilder();
        
        String intensity = "";
        String type = weather;
        
        // Verificar modificadores no início
        if (weather.startsWith("RE")) {
            result.append("Recente ");
            type = weather.substring(2);
        } else if (weather.startsWith("VC")) {
            result.append("Nas proximidades ");
            type = weather.substring(2);
        }
        
        // Verificar intensidade
        if (type.startsWith("+") || type.startsWith("-")) {
            intensity = type.substring(0, 1);
            type = type.substring(1);
        }
        
        String intensityText = INTENSITY_CODES.getOrDefault(intensity, "");
        if (!intensityText.isEmpty()) {
            result.append(intensityText).append(" ");
        }
        
        String weatherText = WEATHER_CODES.getOrDefault(type, type);
        result.append(weatherText);
        
        return result.toString();
    }
    
    private String decodeCloud(String cloud) {
        if (cloud.matches("(SKC|CLR|NSC|NCD)")) {
            return "☁️ " + CLOUD_CODES.getOrDefault(cloud, cloud);
        }
        
        if (cloud.length() < 3) {
            return "☁️ " + cloud;
        }
        
        String type = cloud.substring(0, 3);
        String height = "";
        String special = "";
        
        if (cloud.length() > 3) {
            height = cloud.substring(3, Math.min(6, cloud.length()));
        }
        
        if (cloud.length() > 6) {
            special = cloud.substring(6);
        }
        
        StringBuilder result = new StringBuilder("☁️ ");
        result.append(CLOUD_CODES.getOrDefault(type, type));
        
        if (height.matches("\\d{3}")) {
            int feet = Integer.parseInt(height) * 100;
            result.append(" a ").append(feet).append(" pés");
        } else if ("///".equals(height)) {
            result.append(" (altura não disponível)");
        }
        
        if ("CB".equals(special)) {
            result.append(" (Cumulonimbus)");
        } else if ("TCU".equals(special)) {
            result.append(" (Cumulus torrejante)");
        }
        
        return result.toString();
    }
    
    private String decodeTemperature(String temp) {
        if (temp == null || temp.length() < 5) {
            return "🌡️ Temperatura: Dados incompletos";
        }
        
        String[] parts = temp.split("/");
        if (parts.length != 2) {
            return "🌡️ Temperatura: " + temp;
        }
        
        String tempStr = parts[0];
        String dewStr = parts[1];
        
        try {
            int temperature = tempStr.startsWith("M") ? -Integer.parseInt(tempStr.substring(1)) : Integer.parseInt(tempStr);
            int dewpoint = dewStr.startsWith("M") ? -Integer.parseInt(dewStr.substring(1)) : Integer.parseInt(dewStr);
            
            return "🌡️ Temperatura do ar: " + temperature + "°C, Ponto de orvalho: " + dewpoint + "°C";
        } catch (NumberFormatException e) {
            return "🌡️ Temperatura: " + temp;
        }
    }
    
    private String decodePressure(String pressure) {
        if (pressure == null || pressure.length() < 5) {
            return "📊 QNH: Dados incompletos";
        }
        
        String type = pressure.substring(0, 1);
        String value = pressure.substring(1);
        
        if ("Q".equals(type)) {
            return "📊 QNH: " + value + " hPa";
        } else if ("A".equals(type) && value.length() >= 4) {
            String inches = value.substring(0, 2) + "." + value.substring(2);
            return "📊 QNH: " + inches + " inHg";
        }
        
        return "📊 QNH: " + pressure;
    }
    
    private String decodeTemperatureForecast(String temp) {
        if (temp == null || temp.length() < 9) {
            return "🌡️ " + temp;
        }
        
        try {
            if (temp.startsWith("TX")) {
                // TX24/1718Z -> tempValue = "24/", time = "1718"
                String tempValue = temp.substring(2, 5);
                String time = temp.substring(5, 9);
                
                // Extrair apenas o valor numérico da temperatura (remover a barra)
                String tempNum = tempValue.replace("/", "");
                int temperature = tempNum.startsWith("M") ? -Integer.parseInt(tempNum.substring(1)) : Integer.parseInt(tempNum);
                
                return "🌡️ Temperatura máxima: " + temperature + "°C às " + time + "Z";
            } else if (temp.startsWith("TN")) {
                // TN15/1709Z -> tempValue = "15/", time = "1709"
                String tempValue = temp.substring(2, 5);
                String time = temp.substring(5, 9);
                
                // Extrair apenas o valor numérico da temperatura (remover a barra)
                String tempNum = tempValue.replace("/", "");
                int temperature = tempNum.startsWith("M") ? -Integer.parseInt(tempNum.substring(1)) : Integer.parseInt(tempNum);
                
                return "🌡️ Temperatura mínima: " + temperature + "°C às " + time + "Z";
            }
        } catch (NumberFormatException e) {
            return "🌡️ " + temp;
        }
        
        return "🌡️ " + temp;
    }
    
    private String decodeTrend(String[] parts, int startIndex) {
        if (startIndex >= parts.length) {
            return "📈 Tendência: Dados incompletos";
        }
        
        StringBuilder result = new StringBuilder("📈 Tendência: ");
        
        if (parts[startIndex].startsWith("BECMG")) {
            result.append("Mudança gradual definitiva ");
            if (startIndex + 1 < parts.length) {
                String time = parts[startIndex + 1];
                if (time.matches("\\d{4}/\\d{4}")) {
                    result.append("de ").append(time.substring(0, 4)).append("Z a ").append(time.substring(5, 9)).append("Z");
                }
            }
        } else if (parts[startIndex].startsWith("TEMPO")) {
            result.append("Mudança temporária ");
            if (startIndex + 1 < parts.length) {
                String time = parts[startIndex + 1];
                if (time.matches("\\d{4}/\\d{4}")) {
                    result.append("de ").append(time.substring(0, 4)).append("Z a ").append(time.substring(5, 9)).append("Z");
                }
            }
        } else if (parts[startIndex].startsWith("PROB")) {
            String prob = parts[startIndex].substring(4);
            result.append("Probabilidade de ").append(prob).append("% ");
            if (startIndex + 1 < parts.length) {
                String time = parts[startIndex + 1];
                if (time.matches("\\d{4}/\\d{4}")) {
                    result.append("de ").append(time.substring(0, 4)).append("Z a ").append(time.substring(5, 9)).append("Z");
                }
            }
        } else if (parts[startIndex].startsWith("FM")) {
            result.append("A partir de ").append(parts[startIndex].substring(2)).append("Z");
        } else if (parts[startIndex].startsWith("TL")) {
            result.append("Até ").append(parts[startIndex].substring(2)).append("Z");
        } else if (parts[startIndex].startsWith("AT")) {
            result.append("Em ").append(parts[startIndex].substring(2)).append("Z");
        }
        
        return result.toString();
    }
    
    private int processTrendConditions(String[] parts, int startIndex, StringBuilder decoded) {
        int index = startIndex;
        
        // Processar condições meteorológicas até encontrar próxima tendência ou fim
        while (index < parts.length) {
            String part = parts[index];
            
            // Verificar se é uma nova tendência
            if (part.startsWith("BECMG") || part.startsWith("TEMPO") || part.startsWith("PROB") || 
                part.startsWith("FM") || part.startsWith("TL") || part.startsWith("AT") || 
                part.equals("RMK")) {
                break;
            }
            
            // Processar vento
            if (part.matches("\\d{3}\\d{2}G?\\d{0,2}KT")) {
                decoded.append(decodeWind(part)).append("\n");
                index++;
            }
            // Processar visibilidade
            else if (part.matches("\\d{4}") || part.matches("\\d{2}SM") || "CAVOK".equals(part) || "////".equals(part)) {
                decoded.append(decodeVisibility(part)).append("\n");
                index++;
            }
            // Processar tempo presente
            else if (part.matches("[+-]?[A-Z]{2,3}")) {
                decoded.append(decodeWeather(part)).append("\n");
                index++;
            }
            // Processar nuvens
            else if (part.matches("(FEW|SCT|BKN|OVC|SKC|CLR|NSC|NCD)\\d{0,3}(CB|TCU)?")) {
                decoded.append(decodeCloud(part)).append("\n");
                index++;
            }
            // Processar temperaturas
            else if (part.startsWith("TX") || part.startsWith("TN")) {
                decoded.append(decodeTemperatureForecast(part)).append("\n");
                index++;
            }
            else {
                index++;
            }
        }
        
        return index;
    }
}
