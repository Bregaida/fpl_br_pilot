package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.PubAipDto;
import br.com.fplbr.pilot.aisweb.application.dto.PubAipItemDto;
import br.com.fplbr.pilot.aisweb.infrastructure.util.TsUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.UrlUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser para dados AIP (pub type=AIP) do AISWEB.
 * 
 * Regras principais:
 * - <file>: decodificar & cortar ">; extrair public_id e ext; validar p= (AMDT/Completa/ENRC)
 * - amdt {ts ...} → amdt_effective (YYYY-MM-DD)
 * - amdt_number ⇒ derivar amdt_sequence (primeiro número) e amdt_year (YY < 70 → 2000+YY, senão 1900+YY)
 * - Deduplicação por (group_id, package, amdt_effective, public_id); variant_index sequencial por grupo/pacote/número
 */
@ApplicationScoped
public class PubAipParser {

    private static final Pattern AMDT_NUMBER_PATTERN = Pattern.compile("(\\d+)(?:[A-Z]\\d+)?");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PubAipDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        List<PubAipItemDto> items = new ArrayList<>();
        NodeList fileNodes = doc.getElementsByTagName("file");
        
        Map<String, Integer> variantCounters = new HashMap<>();
        
        for (int i = 0; i < fileNodes.getLength(); i++) {
            Element file = (Element) fileNodes.item(i);
            PubAipItemDto item = parseFile(file, variantCounters);
            if (item != null) {
                items.add(item);
            }
        }
        
        return new PubAipDto(items);
    }

    private PubAipItemDto parseFile(Element file, Map<String, Integer> variantCounters) {
        String linkRaw = XmlUtils.textTrim(file);
        if (linkRaw == null) {
            return null;
        }
        
        String downloadUrl = UrlUtils.decodeCdataAndCut(linkRaw);
        if (downloadUrl == null) {
            return null;
        }
        
        String publicId = UrlUtils.extractPublicId(downloadUrl);
        String ext = UrlUtils.extractExtension(downloadUrl);
        String p = UrlUtils.extractQueryP(downloadUrl);
        
        // Validar p= (AMDT/Completa/ENRC)
        if (p == null || (!p.equals("AMDT") && !p.equals("Completa") && !p.equals("ENRC"))) {
            return null;
        }
        
        // Parse amdt
        String amdtStr = XmlUtils.attr(file, "amdt");
        LocalDate amdtEffective = null;
        if (amdtStr != null) {
            amdtEffective = TsUtils.parseTs(amdtStr) != null ? 
                TsUtils.parseTs(amdtStr).toLocalDate() : null;
        }
        
        // Parse amdt_number
        String amdtNumber = XmlUtils.attr(file, "amdt_number");
        Integer amdtSequence = null;
        Integer amdtYear = null;
        
        if (amdtNumber != null) {
            Matcher matcher = AMDT_NUMBER_PATTERN.matcher(amdtNumber);
            if (matcher.find()) {
                amdtSequence = Integer.parseInt(matcher.group(1));
                
                // Extrair ano do número (simplificado)
                if (amdtNumber.length() >= 4) {
                    String yearStr = amdtNumber.substring(0, 2);
                    try {
                        int year = Integer.parseInt(yearStr);
                        amdtYear = year < 70 ? 2000 + year : 1900 + year;
                    } catch (NumberFormatException e) {
                        // Ignore parsing error
                    }
                }
            }
        }
        
        // Parse group_id e package
        String groupId = XmlUtils.attr(file, "group_id");
        String packageName = XmlUtils.attr(file, "package");
        
        // Calcular variant_index
        String dedupKey = String.format("%s_%s_%s_%s", 
            groupId != null ? groupId : "", 
            packageName != null ? packageName : "",
            amdtEffective != null ? amdtEffective.toString() : "",
            publicId != null ? publicId : ""
        );
        
        int variantIndex = variantCounters.getOrDefault(dedupKey, 0);
        variantCounters.put(dedupKey, variantIndex + 1);
        
        return new PubAipItemDto(
            groupId, packageName, amdtEffective, publicId, ext, p, amdtNumber,
            amdtSequence, amdtYear, variantIndex
        );
    }
}
