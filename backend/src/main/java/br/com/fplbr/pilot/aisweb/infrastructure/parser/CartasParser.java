package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.infrastructure.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para dados de cartas aeronáuticas do AISWEB.
 * 
 * Regras principais:
 * - Cabeçalho <cartas emenda= lastupdate= total=>; converter {ts ...}→ISO UTC
 * - Itens <item>: id, especie/tipo/tipo_descr/nome, IcaoCode, dt, dtPublic, amdt, use, pe→has_special_procedures, suplementos@count
 * - <link>: decodificar & e cortar após ">; armazenar download.url e file_slug; format=pdf
 */
@ApplicationScoped
public class CartasParser {

    public CartasDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        // Parse cabeçalho
        CartasHeaderDto header = parseHeader(doc);
        
        // Parse itens
        List<CartasItemDto> items = parseItems(doc);
        
        return new CartasDto(header, items);
    }

    private CartasHeaderDto parseHeader(Document doc) {
        NodeList cartasNodes = doc.getElementsByTagName("cartas");
        if (cartasNodes.getLength() == 0) {
            return new CartasHeaderDto(null, null, null);
        }
        
        Element cartas = (Element) cartasNodes.item(0);
        
        String emenda = XmlUtils.attr(cartas, "emenda");
        String lastupdateStr = XmlUtils.attr(cartas, "lastupdate");
        String totalStr = XmlUtils.attr(cartas, "total");
        
        LocalDateTime lastupdate = null;
        if (lastupdateStr != null) {
            lastupdate = TsUtils.parseTs(lastupdateStr);
        }
        
        Integer total = null;
        if (totalStr != null) {
            try {
                total = Integer.parseInt(totalStr);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        return new CartasHeaderDto(emenda, lastupdate, total);
    }

    private List<CartasItemDto> parseItems(Document doc) {
        List<CartasItemDto> items = new ArrayList<>();
        NodeList itemNodes = doc.getElementsByTagName("item");
        
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            items.add(parseItem(item));
        }
        
        return items;
    }

    private CartasItemDto parseItem(Element item) {
        String id = getElementText(item, "id");
        String especie = getElementText(item, "especie");
        String tipo = getElementText(item, "tipo");
        String tipoDescr = getElementText(item, "tipo_descr");
        String nome = getElementText(item, "nome");
        String icaoCode = getElementText(item, "IcaoCode");
        
        String dtStr = getElementText(item, "dt");
        String dtPublicStr = getElementText(item, "dtPublic");
        
        LocalDateTime dt = null;
        LocalDateTime dtPublic = null;
        
        if (dtStr != null) {
            dt = TsUtils.parseTs(dtStr);
        }
        if (dtPublicStr != null) {
            dtPublic = TsUtils.parseTs(dtPublicStr);
        }
        
        String amdt = getElementText(item, "amdt");
        String use = getElementText(item, "use");
        
        // Parse pe (procedimentos especiais)
        Boolean hasSpecialProcedures = false;
        String peValue = getElementText(item, "pe");
        if (peValue != null) {
            hasSpecialProcedures = "1".equals(peValue) || "true".equalsIgnoreCase(peValue);
        }
        
        // Parse suplementos count
        Integer suplementosCount = null;
        String suplementosCountStr = XmlUtils.attr(item, "count");
        if (suplementosCountStr != null) {
            try {
                suplementosCount = Integer.parseInt(suplementosCountStr);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        // Parse link
        String downloadUrl = null;
        String fileSlug = null;
        String format = "pdf"; // Default
        
        String linkRaw = getElementText(item, "link");
        if (linkRaw != null) {
            downloadUrl = UrlUtils.decodeCdataAndCut(linkRaw);
            fileSlug = UrlUtils.extractPublicId(downloadUrl);
            format = UrlUtils.extractExtension(downloadUrl);
            if (format == null) {
                format = "pdf";
            }
        }
        
        return new CartasItemDto(
            id, especie, tipo, tipoDescr, nome, icaoCode, dt, dtPublic, amdt, use,
            hasSpecialProcedures, suplementosCount, downloadUrl, fileSlug, format
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
