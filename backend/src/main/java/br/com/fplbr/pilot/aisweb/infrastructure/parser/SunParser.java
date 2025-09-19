package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.infrastructure.util.GeoUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.TsUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.WeekdayUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser para dados do sol do AISWEB.
 * 
 * Regras principais:
 * - date yyyy-MM-dd; sunrise/sunset HH:mm (aceitar HH:mm:ss)
 * - UTC→America/Sao_Paulo
 * - diaSemanaNumero 1..7 validado; weekdayOk
 * - geo "lat,lng" double, validar faixa; geoOk
 * - intervaloOk: sunriseUtc < sunsetUtc
 */
@ApplicationScoped
public class SunParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{2}):(\\d{2})(?::(\\d{2}))?");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SunDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        // Buscar dados do sol no ROTAER
        NodeList days = doc.getElementsByTagName("day");
        if (days.getLength() == 0) {
            return null;
        }
        
        Element day = (Element) days.item(0);
        
        String icao = getElementText(day, "aero");
        String geo = getElementText(day, "geo");
        String dateStr = getElementText(day, "date");
        String sunriseStr = getElementText(day, "sunrise");
        String sunsetStr = getElementText(day, "sunset");
        String weekDayStr = getElementText(day, "weekDay");
        
        // Parse data
        LocalDate date = null;
        if (dateStr != null) {
            try {
                date = LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        // Parse horários
        LocalTime sunrise = parseTime(sunriseStr);
        LocalTime sunset = parseTime(sunsetStr);
        
        // Parse dia da semana
        Integer diaSemanaNumero = null;
        String diaSemanaNome = null;
        Boolean weekdayOk = false;
        
        if (weekDayStr != null) {
            try {
                diaSemanaNumero = Integer.parseInt(weekDayStr);
                if (diaSemanaNumero >= 1 && diaSemanaNumero <= 7) {
                    diaSemanaNome = WeekdayUtils.getDayName(diaSemanaNumero, java.time.format.TextStyle.FULL);
                    weekdayOk = date != null ? date.getDayOfWeek().getValue() == diaSemanaNumero : false;
                }
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        // Parse coordenadas
        Double latitude = null;
        Double longitude = null;
        Boolean geoOk = false;
        
        if (geo != null) {
            double[] coords = GeoUtils.splitLatLng(geo);
            if (coords != null) {
                latitude = coords[0];
                longitude = coords[1];
                geoOk = GeoUtils.isValidLatitude(latitude) && GeoUtils.isValidLongitude(longitude);
            }
        }
        
        // Validar intervalo
        Boolean intervaloOk = false;
        if (sunrise != null && sunset != null) {
            // Converter para UTC para comparação (simplificado)
            LocalDateTime sunriseUtc = LocalDateTime.of(date != null ? date : LocalDate.now(), sunrise);
            LocalDateTime sunsetUtc = LocalDateTime.of(date != null ? date : LocalDate.now(), sunset);
            intervaloOk = sunriseUtc.isBefore(sunsetUtc);
        }
        
        return new SunDto(
            icao, date != null ? LocalDateTime.of(date, LocalTime.MIDNIGHT) : null,
            sunrise, sunset, diaSemanaNumero, diaSemanaNome, weekdayOk,
            latitude, longitude, geoOk, intervaloOk
        );
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = TIME_PATTERN.matcher(timeStr.trim());
        if (matcher.matches()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = Integer.parseInt(matcher.group(2));
            int second = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
            
            try {
                return LocalTime.of(hour, minute, second);
            } catch (Exception e) {
                return null;
            }
        }
        
        return null;
    }
    
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent() != null ? node.getTextContent().trim() : null;
        }
        return null;
    }
}
