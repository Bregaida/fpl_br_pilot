package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.NotamDto;
import br.com.fplbr.pilot.aisweb.application.dto.NotamItemDto;
import br.com.fplbr.pilot.aisweb.application.dto.NotamMetaDto;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser para dados NOTAM do AISWEB.
 * 
 * Regras principais:
 * - Meta: request_id, total, updatedat→ISO UTC
 * - Itens: mapear exatamente os campos do JSON normalizado fornecido
 * - Datas YYMMDDhhmm → ISO UTC
 * - type_raw (NOTAMN/NOTAMR/NOTAMC) → type (N/R/C); lifecycle_state (ACTIVE/CANCELLED/…)
 * - Preservar geo_raw e geo_url sem normalizar coordenadas "exóticas"
 */
@ApplicationScoped
public class NotamParser {

    private static final Pattern NOTAM_DATE_PATTERN = Pattern.compile("(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})");

    public NotamDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        // Parse metadados
        NotamMetaDto meta = parseMeta(doc);
        
        // Parse itens
        List<NotamItemDto> items = parseItems(doc);
        
        return new NotamDto(meta, items);
    }

    private NotamMetaDto parseMeta(Document doc) {
        // Buscar o elemento notam que contém os metadados
        NodeList notamNodes = doc.getElementsByTagName("notam");
        if (notamNodes.getLength() == 0) {
            return new NotamMetaDto(null, 1, null, null, null);
        }
        
        Element notamElement = (Element) notamNodes.item(0);
        String totalStr = notamElement.getAttribute("total");
        
        Integer total = null;
        if (totalStr != null && !totalStr.trim().isEmpty()) {
            try {
                total = Integer.parseInt(totalStr);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        
        return new NotamMetaDto(total, 1, total, null, null);
    }

    private List<NotamItemDto> parseItems(Document doc) {
        List<NotamItemDto> items = new ArrayList<>();
        NodeList itemNodes = doc.getElementsByTagName("item");
        
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            items.add(parseItem(item));
        }
        
        return items;
    }

    private NotamItemDto parseItem(Element item) {
        String id = getElementText(item, "id");
        String icaoCode = getElementText(item, "icaoairport_id");
        String message = getElementText(item, "e"); // Campo 'e' contém a mensagem do NOTAM
        String typeRaw = getElementText(item, "tp"); // Campo 'tp' contém o tipo (NOTAMN/NOTAMR/NOTAMC)
        String type = normalizarType(typeRaw);
        String purpose = getElementText(item, "purpose");
        String scope = getElementText(item, "scope");
        String location = getElementText(item, "loc");
        String status = getElementText(item, "status");
        String fir = getElementText(item, "fir");
        String trafficType = getElementText(item, "traffic");
        String lowerLimit = getElementText(item, "lower");
        String upperLimit = getElementText(item, "upper");
        
        // Construir rawData com informações relevantes
        StringBuilder rawDataBuilder = new StringBuilder();
        rawDataBuilder.append("ID: ").append(id).append("; ");
        rawDataBuilder.append("ICAO: ").append(icaoCode).append("; ");
        rawDataBuilder.append("Type: ").append(typeRaw).append("; ");
        rawDataBuilder.append("Status: ").append(status).append("; ");
        rawDataBuilder.append("Purpose: ").append(purpose).append("; ");
        rawDataBuilder.append("Scope: ").append(scope).append("; ");
        rawDataBuilder.append("Location: ").append(location).append("; ");
        rawDataBuilder.append("Message: ").append(message);
        String rawData = rawDataBuilder.toString();
        
        String validFromStr = getElementText(item, "b"); // Campo 'b' contém data de início
        String validUntilStr = getElementText(item, "c"); // Campo 'c' contém data de fim
        
        LocalDateTime validFrom = parseNotamDate(validFromStr);
        LocalDateTime validUntil = parseNotamDate(validUntilStr);
        
        return new NotamItemDto(
            id, icaoCode, message, type, validFrom, validUntil,
            fir, trafficType, purpose, scope, lowerLimit, upperLimit, location, status, rawData
        );
    }

    private String normalizarType(String typeRaw) {
        if (typeRaw == null) {
            return null;
        }
        
        String typeUpper = typeRaw.toUpperCase();
        if (typeUpper.contains("NOTAMN")) {
            return "N";
        } else if (typeUpper.contains("NOTAMR")) {
            return "R";
        } else if (typeUpper.contains("NOTAMC")) {
            return "C";
        }
        return typeRaw;
    }


    private LocalDateTime parseNotamDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        // Parse formato YYMMDDhhmm
        Matcher matcher = NOTAM_DATE_PATTERN.matcher(dateStr.trim());
        if (matcher.matches()) {
            try {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));
                int hour = Integer.parseInt(matcher.group(4));
                int minute = Integer.parseInt(matcher.group(5));
                
                // Converter ano de 2 dígitos para 4 dígitos
                int fullYear = year < 70 ? 2000 + year : 1900 + year;
                
                return LocalDateTime.of(fullYear, month, day, hour, minute);
            } catch (Exception e) {
                // Ignore parsing error
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
