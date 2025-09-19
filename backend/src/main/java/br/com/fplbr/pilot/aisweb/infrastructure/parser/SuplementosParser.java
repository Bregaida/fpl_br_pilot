package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.SuplementosDto;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosItemDto;
import br.com.fplbr.pilot.aisweb.application.dto.SuplementosMetaDto;
import br.com.fplbr.pilot.aisweb.infrastructure.util.TsUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import java.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para dados de suplementos do AISWEB.
 * 
 * Regras principais:
 * - Raiz <suplementos>: apiID, lastupdate {ts ...}→ISO, recordcount, total
 * - <item>: id_attr (atributo) vs <id> (corpo) — se diferentes, manter ambos e flag id_mismatch
 * - Campos: status + status_code (em vigor→ACTIVE, cancelado→CANCELLED, outros→OTHER)
 * - n→number + number_padded (4 dígitos); serie (1 char upper) + series_number_label "N 68/2025"
 * - tipo normalizado (AIRAC/COMUM); local (ICAO 4 letras)
 * - <dt> (AIRAC) YYYY-MM-DD; <data_inicio>/<data_fim> {ts ...}→ISO UTC
 * - ref com parsing AIP {SEC} {subsec}; anexo vazio→null; year a partir de <dt>
 */
@ApplicationScoped
public class SuplementosParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SuplementosDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        // Parse metadados
        SuplementosMetaDto meta = parseMeta(doc);
        
        // Parse itens
        List<SuplementosItemDto> items = parseItems(doc);
        
        return new SuplementosDto(meta, items);
    }

    private SuplementosMetaDto parseMeta(Document doc) {
        NodeList suplementosNodes = doc.getElementsByTagName("suplementos");
        if (suplementosNodes.getLength() == 0) {
            return new SuplementosMetaDto(null, null, null, null, null);
        }
        
        Element suplementos = (Element) suplementosNodes.item(0);
        
        String totalStr = XmlUtils.attr(suplementos, "total");
        
        Integer total = null;
        if (totalStr != null) {
            try {
                total = Integer.parseInt(totalStr);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        return new SuplementosMetaDto(total, 1, total, null, null);
    }

    private List<SuplementosItemDto> parseItems(Document doc) {
        List<SuplementosItemDto> items = new ArrayList<>();
        NodeList itemNodes = doc.getElementsByTagName("item");
        
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            items.add(parseItem(item));
        }
        
        return items;
    }

    private SuplementosItemDto parseItem(Element item) {
        // Parse IDs
        String idAttr = XmlUtils.attr(item, "id");
        String id = getElementText(item, "id");
        
        // Parse status
        String status = getElementText(item, "status");
        
        // Parse número e série
        String n = getElementText(item, "n");
        String serie = getElementText(item, "serie");
        
        // Parse tipo
        String tipo = getElementText(item, "tipo");
        
        // Parse local
        String local = getElementText(item, "local");
        
        // Parse datas
        String dtStr = getElementText(item, "dt");
        String dataInicioStr = getElementText(item, "data_inicio");
        String dataFimStr = getElementText(item, "data_fim");
        
        LocalDate dt = null;
        if (dtStr != null) {
            try {
                dt = LocalDate.parse(dtStr, DATE_FORMATTER);
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        LocalDateTime dataInicio = null;
        LocalDateTime dataFim = null;
        
        if (dataInicioStr != null) {
            dataInicio = TsUtils.parseTs(dataInicioStr);
        }
        if (dataFimStr != null) {
            dataFim = TsUtils.parseTs(dataFimStr);
        }
        
        // Parse ref
        String ref = getElementText(item, "ref");
        
        // Parse anexo
        String anexo = getElementText(item, "anexo");
        if (anexo != null && anexo.trim().isEmpty()) {
            anexo = null;
        }
        
        // Parse campos importantes adicionais
        String titulo = getElementText(item, "titulo");
        String texto = getElementText(item, "texto");
        String duracao = getElementText(item, "duracao");
        
        return new SuplementosItemDto(
            idAttr, local, tipo, null, id, null,
            dataInicio != null ? dataInicio.toLocalDate() : null, 
            dataFim != null ? dataFim.toLocalDate() : null, 
            status, null, null, null,
            null, dt, null, null,
            // Novos campos importantes
            titulo, texto, duracao, ref, anexo,
            n, serie, local, dtStr, dataInicioStr, dataFimStr
        );
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