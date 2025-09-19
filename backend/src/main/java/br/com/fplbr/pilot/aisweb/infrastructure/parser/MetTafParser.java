package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser para dados meteorológicos (MET/TAF) do AISWEB.
 * 
 * Regras principais:
 * - Guardar raw (metarRaw, tafRaw) e decodificado (mensagens legíveis)
 * - METAR: tipo, horário, vento (variável dddVddd), CAVOK ⇒ prevailing_m=10000
 * - TAF: issued, validity, condições iniciais, TN/TX, mudanças BECMG/TEMPO/PROBxx
 */
@ApplicationScoped
public class MetTafParser {

    // Padrões para parsing correto de METAR/TAF
    private static final Pattern PTN_TIME = Pattern.compile("\\b(\\d{6})Z\\b");      // ex: 161300Z
    private static final Pattern PTN_WIND_DIRSPD = Pattern.compile("\\b(\\d{3}|VRB)(\\d{2})KT\\b");
    private static final Pattern PTN_VIS = Pattern.compile("\\b(\\d{4})\\b");        // 4 dígitos = metros (simplificado)
    private static final Pattern PTN_QNH = Pattern.compile("\\bQ(\\d{4})\\b");       // Q1018
    private static final Pattern PTN_CLOUD = Pattern.compile("\\b(FEW|SCT|BKN|OVC)(\\d{3})(CB|TCU)?\\b");
    private static final Pattern PTN_TEMP = Pattern.compile("\\b(\\d{2})/(\\d{2})\\b"); // 21/13
    
    // Padrões para TAF
    private static final Pattern PTN_TAF_ISSUED = Pattern.compile("\\b(\\d{6})Z\\b");      // 160900Z
    private static final Pattern PTN_TAF_VALID = Pattern.compile("\\b(\\d{2})(\\d{2})/(\\d{2})(\\d{2})\\b"); // 1612/1624
    private static final Pattern PTN_TX = Pattern.compile("\\bTX(\\d{2})/(\\d{4})Z\\b");
    private static final Pattern PTN_TN = Pattern.compile("\\bTN(\\d{2})/(\\d{4})Z\\b");
    
    // Padrões para mudanças TAF
    private static final Pattern PTN_BECMG = Pattern.compile("\\bBECMG\\s+(\\d{4})/(\\d{4})\\s+(.+?)(?=\\s+(?:BECMG|TEMPO|PROB|RMK|$))");
    private static final Pattern PTN_TEMPO = Pattern.compile("\\bTEMPO\\s+(\\d{4})/(\\d{4})\\s+(.+?)(?=\\s+(?:BECMG|TEMPO|PROB|RMK|$))");
    private static final Pattern PTN_PROB = Pattern.compile("\\bPROB(\\d{2})\\s+(\\d{4})/(\\d{4})\\s+(.+?)(?=\\s+(?:BECMG|TEMPO|PROB|RMK|$))");

    public MetarDto parseMetar(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            System.out.println("❌ [METAR-PARSER] XML é null ou vazio");
            return null;
        }

        // Extrair texto METAR do XML
        String metarText = extractMetarText(xml);
        if (metarText == null || metarText.trim().isEmpty()) {
            return null;
        }

        String cleanMetar = metarText.trim();
        
        // Parse campos do METAR
        String tipo = "METAR";
        LocalDateTime horario = parseMetarTime(cleanMetar, ZoneId.of("UTC"));
        VentoDto vento = parseWind(cleanMetar);
        Integer prevailingM = parseVisMeters(cleanMetar);
        List<NuvemDto> clouds = parseClouds(cleanMetar);
        List<String> weather = new ArrayList<>(); // Vazio por enquanto
        Integer temperatura = null;
        Integer dewpoint = null;
        Integer qnh = parseQnh(cleanMetar);
        
        // Parse temperatura/dewpoint
        Matcher tempMatcher = PTN_TEMP.matcher(cleanMetar);
        if (tempMatcher.find()) {
            temperatura = Integer.parseInt(tempMatcher.group(1));
            dewpoint = Integer.parseInt(tempMatcher.group(2));
        }
        
        MetarDto result = new MetarDto(
            cleanMetar, tipo, horario, vento, prevailingM, clouds, weather,
            temperatura, dewpoint, qnh != null ? qnh.doubleValue() : null, null
        );
        
        return result;
    }

    public TafDto parseTaf(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return null;
        }

        // Extrair texto TAF do XML
        String tafText = extractTafText(xml);
        if (tafText == null || tafText.trim().isEmpty()) {
            return null;
        }

        String cleanTaf = tafText.trim();
        
        // Parse campos do TAF
        LocalDateTime issued = tafIssued(cleanTaf, ZoneId.of("UTC"));
        String validity = tafValidity(cleanTaf);
        CondicaoInicialDto condicoesIniciais = parseCondicoesIniciais(cleanTaf);
        Integer tn = tafTN(cleanTaf);
        Integer tx = tafTX(cleanTaf);
        List<MudancaDto> mudancas = parseMudancas(cleanTaf);
        
        return new TafDto(
            cleanTaf, issued, validity, condicoesIniciais, tn, tx, mudancas
        );
    }

    // Métodos auxiliares para extrair textos do XML
    private String extractMetarText(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Primeiro, tentar buscar por padrão METAR no texto (mais confiável)
            Pattern metarPattern = Pattern.compile("METAR[^=]+=");
            Matcher matcher = metarPattern.matcher(xml);
            if (matcher.find()) {
                String metarText = matcher.group(0).replace("=", "").trim();
                return metarText;
            }
            
            // Fallback: tentar parsear XML
            Document doc = XmlUtils.createDocument(xml);
            NodeList metarNodes = doc.getElementsByTagName("metar");
            if (metarNodes.getLength() > 0) {
                String metarText = metarNodes.item(0).getTextContent().trim();
                return metarText;
            }
            
            // Tentar outras tags possíveis
            String[] possibleTags = {"metarRaw", "metar_text", "metarText", "raw"};
            for (String tag : possibleTags) {
                NodeList nodes = doc.getElementsByTagName(tag);
                if (nodes.getLength() > 0) {
                    String text = nodes.item(0).getTextContent().trim();
                    if (text.startsWith("METAR")) {
                        return text;
                    }
                }
            }
            
        } catch (Exception e) {
            // Log de erro silencioso
        }
        return null;
    }

    private String extractTafText(String xml) {
        try {
            Document doc = XmlUtils.createDocument(xml);
            NodeList tafNodes = doc.getElementsByTagName("taf");
            if (tafNodes.getLength() > 0) {
                return tafNodes.item(0).getTextContent();
            }
        } catch (Exception e) {
            // Fallback: buscar por padrão TAF no texto
            Pattern tafPattern = Pattern.compile("TAF[^=]+=");
            Matcher matcher = tafPattern.matcher(xml);
            if (matcher.find()) {
                return matcher.group(0).replace("=", "");
            }
        }
        return null;
    }

    // Métodos auxiliares para parsing correto
    private Integer parseQnh(String metar) {
        Matcher m = PTN_QNH.matcher(metar);
        return m.find() ? Integer.parseInt(m.group(1)) : null; // 1018
    }

    private Integer parseVisMeters(String metar) {
        // pega o primeiro grupo de 4 dígitos (ex.: 8000)
        Matcher m = PTN_VIS.matcher(metar);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    private VentoDto parseWind(String metar) {
        Matcher m = PTN_WIND_DIRSPD.matcher(metar);
        if (!m.find()) return null;
        String dirTxt = m.group(1);
        Integer spd = Integer.parseInt(m.group(2));
        Integer dir = "VRB".equals(dirTxt) ? null : Integer.valueOf(dirTxt);
        return new VentoDto(dir, spd, null, null);
    }

    private List<NuvemDto> parseClouds(String metar) {
        List<NuvemDto> list = new ArrayList<>();
        Matcher m = PTN_CLOUD.matcher(metar);
        while (m.find()) {
            String tipo = m.group(1); // FEW/SCT/BKN/OVC
            int hundreds = Integer.parseInt(m.group(2));
            Integer baseFeet = hundreds * 100; // 030 -> 3000 ft
            list.add(new NuvemDto(tipo, baseFeet));
        }
        return list;
    }

    private LocalDateTime parseMetarTime(String metar, ZoneId zone) {
        Matcher m = PTN_TIME.matcher(metar);
        if (!m.find()) return null; // ex.: 161300Z
        String hhmm = m.group(1); // ddHHmm (na prática: 161300 = dia 16, 13:00)
        int day = Integer.parseInt(hhmm.substring(0,2));
        int hour = Integer.parseInt(hhmm.substring(2,4));
        int min = Integer.parseInt(hhmm.substring(4,6));
        // Constrói no mês/ano atuais (assumindo "issued" do mês corrente)
        LocalDate today = LocalDate.now(zone);
        LocalDate base = today.withDayOfMonth(Math.min(day, today.lengthOfMonth()));
        return LocalDateTime.of(base, LocalTime.of(hour, min));
    }

    // Métodos para TAF
    private LocalDateTime tafIssued(String taf, ZoneId zone) {
        Matcher m = PTN_TAF_ISSUED.matcher(taf);
        if (!m.find()) return null;
        String s = m.group(1); // ddHHmm
        int day = Integer.parseInt(s.substring(0,2));
        int hh = Integer.parseInt(s.substring(2,4));
        int mm = Integer.parseInt(s.substring(4,6));
        LocalDate today = LocalDate.now(zone);
        LocalDate base = today.withDayOfMonth(Math.min(day, today.lengthOfMonth()));
        return LocalDateTime.of(base, LocalTime.of(hh, mm));
    }

    private String tafValidity(String taf) {
        Matcher m = PTN_TAF_VALID.matcher(taf);
        return m.find() ? (m.group(1)+m.group(2)+"/"+m.group(3)+m.group(4)) : null; // "1612/1624"
    }

    private Integer tafTX(String taf) {
        Matcher m = PTN_TX.matcher(taf);
        return m.find() ? Integer.parseInt(m.group(1)) : null; // 27
    }

    private Integer tafTN(String taf) {
        Matcher m = PTN_TN.matcher(taf);
        return m.find() ? Integer.parseInt(m.group(1)) : null; // 18
    }

    private CondicaoInicialDto parseCondicoesIniciais(String taf) {
        VentoDto vento = parseWind(taf);
        Integer visibilidade = parseVisMeters(taf);
        List<NuvemDto> nuvens = parseClouds(taf);
        
        return new CondicaoInicialDto(vento, visibilidade, nuvens);
    }
    
    private List<MudancaDto> parseMudancas(String taf) {
        List<MudancaDto> mudancas = new ArrayList<>();
        
        // Parse BECMG (mudanças graduais)
        Matcher becmgMatcher = PTN_BECMG.matcher(taf);
        while (becmgMatcher.find()) {
            String inicio = becmgMatcher.group(1);
            String fim = becmgMatcher.group(2);
            String condicoes = becmgMatcher.group(3).trim();
            
            mudancas.add(new MudancaDto(
                "BECMG",
                inicio + "/" + fim,
                parseCondicoesMudanca(condicoes)
            ));
        }
        
        // Parse TEMPO (mudanças temporárias)
        Matcher tempoMatcher = PTN_TEMPO.matcher(taf);
        while (tempoMatcher.find()) {
            String inicio = tempoMatcher.group(1);
            String fim = tempoMatcher.group(2);
            String condicoes = tempoMatcher.group(3).trim();
            
            mudancas.add(new MudancaDto(
                "TEMPO",
                inicio + "/" + fim,
                parseCondicoesMudanca(condicoes)
            ));
        }
        
        // Parse PROB (mudanças probabilísticas)
        Matcher probMatcher = PTN_PROB.matcher(taf);
        while (probMatcher.find()) {
            String probabilidade = probMatcher.group(1);
            String inicio = probMatcher.group(2);
            String fim = probMatcher.group(3);
            String condicoes = probMatcher.group(4).trim();
            
            mudancas.add(new MudancaDto(
                "PROB" + probabilidade,
                inicio + "/" + fim,
                parseCondicoesMudanca(condicoes)
            ));
        }
        
        return mudancas;
    }
    
    private CondicaoInicialDto parseCondicoesMudanca(String condicoes) {
        VentoDto vento = parseWind(condicoes);
        Integer visibilidade = parseVisMeters(condicoes);
        List<NuvemDto> nuvens = parseClouds(condicoes);
        
        return new CondicaoInicialDto(vento, visibilidade, nuvens);
    }
}
