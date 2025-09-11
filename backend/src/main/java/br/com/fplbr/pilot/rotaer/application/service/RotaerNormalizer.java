package br.com.fplbr.pilot.rotaer.application.service;

import br.com.fplbr.pilot.rotaer.domain.model.RotaerData;
import br.com.fplbr.pilot.rotaer.domain.model.ValidationWarning;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizador de dados do ROTAER
 * Converte formatos diversos para o formato canônico
 */
@ApplicationScoped
public class RotaerNormalizer {
    
    private static final Logger log = LoggerFactory.getLogger(RotaerNormalizer.class);
    
    // Regex para coordenadas DMS
    private static final Pattern DMS_PATTERN = Pattern.compile(
        "([0-9]{1,2})\\s*([0-9]{1,2})\\s*([0-9]{1,2}(?:\\.[0-9]+)?)\\s*([NS])" +
        "\\s*/?\\s*" +
        "([0-9]{1,3})\\s*([0-9]{1,2})\\s*([0-9]{1,2}(?:\\.[0-9]+)?)\\s*([EW])"
    );
    
    // Regex para coordenadas compactas
    private static final Pattern COMPACT_DMS_PATTERN = Pattern.compile(
        "([0-9]{1,2})([0-9]{2})\\.([0-9]{2})\\s*([NS])" +
        "\\s*/?\\s*" +
        "([0-9]{1,3})([0-9]{2})\\.([0-9]{2})\\s*([EW])"
    );
    
    // Regex para dimensões de pista
    private static final Pattern DIMENSIONS_PATTERN = Pattern.compile("([0-9]+)x([0-9]+)");
    
    // Regex para PCN
    private static final Pattern PCN_PATTERN = Pattern.compile("([0-9]+)/([RF])/([ABCD])/([WXYZ])/([TU])");
    
    // Regex para L-codes com ângulo
    private static final Pattern L_CODE_ANGLE_PATTERN = Pattern.compile("L([0-9]+)([A-Z])?\\(([0-9.]+)\\)");
    
    // Regex para L-codes simples
    private static final Pattern L_CODE_SIMPLE_PATTERN = Pattern.compile("L([0-9]+)([A-Z])?");
    
    // Regex para designadores de pista
    private static final Pattern RUNWAY_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[0-6])");
    
    // Regex para frequências
    private static final Pattern FREQUENCY_PATTERN = Pattern.compile("([0-9]{3})\\.([0-9]{3})");
    
    // Regex para INFOTEMP ID
    private static final Pattern INFOTEMP_ID_PATTERN = Pattern.compile("^([FRCM])([0-9]{4})/([0-9]{4})$");
    
    /**
     * Normaliza coordenadas DMS para decimal
     */
    public RotaerData.Coordenadas normalizeCoordinates(String coordText, List<ValidationWarning> warnings) {
        if (coordText == null || coordText.trim().isEmpty()) {
            warnings.add(new ValidationWarning("coordenadas", coordText, "coordenadas_obrigatorias", 
                                             ValidationWarning.Severity.ERROR, "Coordenadas são obrigatórias"));
            return null;
        }
        
        String normalized = coordText.trim().toUpperCase();
        
        // Tentar formato padrão primeiro
        Matcher matcher = DMS_PATTERN.matcher(normalized);
        if (matcher.matches()) {
            return parseDmsCoordinates(matcher, warnings);
        }
        
        // Tentar formato compacto
        matcher = COMPACT_DMS_PATTERN.matcher(normalized);
        if (matcher.matches()) {
            return parseCompactDmsCoordinates(matcher, warnings);
        }
        
        warnings.add(new ValidationWarning("coordenadas", coordText, "formato_invalido", 
                                         ValidationWarning.Severity.ERROR, 
                                         "Formato de coordenadas não reconhecido. Use: DD MM SS.S H / DDD MM SS.S H"));
        return null;
    }
    
    private RotaerData.Coordenadas parseDmsCoordinates(Matcher matcher, List<ValidationWarning> warnings) {
        try {
            int latDeg = Integer.parseInt(matcher.group(1));
            int latMin = Integer.parseInt(matcher.group(2));
            double latSec = Double.parseDouble(matcher.group(3));
            String latHem = matcher.group(4);
            
            int lonDeg = Integer.parseInt(matcher.group(5));
            int lonMin = Integer.parseInt(matcher.group(6));
            double lonSec = Double.parseDouble(matcher.group(7));
            String lonHem = matcher.group(8);
            
            double latDd = convertDmsToDecimal(latDeg, latMin, latSec, latHem);
            double lonDd = convertDmsToDecimal(lonDeg, lonMin, lonSec, lonHem);
            
            String latDms = String.format("%02d %02d %02.0f%s", latDeg, latMin, latSec, latHem);
            String lonDms = String.format("%03d %02d %02.0f%s", lonDeg, lonMin, lonSec, lonHem);
            
            return new RotaerData.Coordenadas(latDms, lonDms, latDd, lonDd);
            
        } catch (Exception e) {
            warnings.add(new ValidationWarning("coordenadas", matcher.group(0), "conversao_falhou", 
                                             ValidationWarning.Severity.ERROR, 
                                             "Erro ao converter coordenadas: " + e.getMessage()));
            return null;
        }
    }
    
    private RotaerData.Coordenadas parseCompactDmsCoordinates(Matcher matcher, List<ValidationWarning> warnings) {
        try {
            int latDeg = Integer.parseInt(matcher.group(1));
            int latMin = Integer.parseInt(matcher.group(2));
            double latSec = Double.parseDouble(matcher.group(3)) * 60.0 / 100.0; // Converter centésimos para segundos
            String latHem = matcher.group(4);
            
            int lonDeg = Integer.parseInt(matcher.group(5));
            int lonMin = Integer.parseInt(matcher.group(6));
            double lonSec = Double.parseDouble(matcher.group(7)) * 60.0 / 100.0; // Converter centésimos para segundos
            String lonHem = matcher.group(8);
            
            double latDd = convertDmsToDecimal(latDeg, latMin, latSec, latHem);
            double lonDd = convertDmsToDecimal(lonDeg, lonMin, lonSec, lonHem);
            
            String latDms = String.format("%02d %02d %05.2f%s", latDeg, latMin, latSec, latHem);
            String lonDms = String.format("%03d %02d %05.2f%s", lonDeg, lonMin, lonSec, lonHem);
            
            return new RotaerData.Coordenadas(latDms, lonDms, latDd, lonDd);
            
        } catch (Exception e) {
            warnings.add(new ValidationWarning("coordenadas", matcher.group(0), "conversao_falhou", 
                                             ValidationWarning.Severity.ERROR, 
                                             "Erro ao converter coordenadas compactas: " + e.getMessage()));
            return null;
        }
    }
    
    private double convertDmsToDecimal(int degrees, int minutes, double seconds, String hemisphere) {
        double decimal = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if ("S".equals(hemisphere) || "W".equals(hemisphere)) {
            decimal = -decimal;
        }
        return decimal;
    }
    
    /**
     * Normaliza dimensões de pista
     */
    public RotaerData.DimensoesPista normalizeDimensions(String dimensionsText, List<ValidationWarning> warnings) {
        if (dimensionsText == null || dimensionsText.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = DIMENSIONS_PATTERN.matcher(dimensionsText.trim());
        if (matcher.matches()) {
            try {
                int comprimento = Integer.parseInt(matcher.group(1));
                int largura = Integer.parseInt(matcher.group(2));
                return new RotaerData.DimensoesPista(comprimento, largura);
            } catch (NumberFormatException e) {
                warnings.add(new ValidationWarning("dimensoes", dimensionsText, "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, 
                                                 "Erro ao converter dimensões: " + e.getMessage()));
            }
        } else {
            warnings.add(new ValidationWarning("dimensoes", dimensionsText, "formato_invalido", 
                                             ValidationWarning.Severity.WARNING, 
                                             "Formato de dimensões não reconhecido. Use: LARGURAxALTURA"));
        }
        
        return null;
    }
    
    /**
     * Normaliza PCN
     */
    public String normalizePcn(String pcnText, List<ValidationWarning> warnings) {
        if (pcnText == null || pcnText.trim().isEmpty()) {
            return null;
        }
        
        String normalized = pcnText.trim().toUpperCase();
        Matcher matcher = PCN_PATTERN.matcher(normalized);
        
        if (matcher.matches()) {
            return normalized; // PCN já está no formato correto
        } else {
            warnings.add(new ValidationWarning("pcn", pcnText, "formato_invalido", 
                                             ValidationWarning.Severity.WARNING, 
                                             "Formato PCN não reconhecido. Use: NN/(R|F)/(A|B|C|D)/(W|X|Y|Z)/(T|U)"));
            return pcnText; // Retornar original para preservar informação
        }
    }
    
    /**
     * Normaliza L-codes
     */
    public List<String> normalizeLights(String lightsText, List<ValidationWarning> warnings) {
        List<String> lights = new ArrayList<>();
        
        if (lightsText == null || lightsText.trim().isEmpty()) {
            return lights;
        }
        
        String[] parts = lightsText.split("[,\\s]+");
        for (String part : parts) {
            String light = part.trim();
            if (light.isEmpty()) continue;
            
            // Verificar se tem ângulo
            Matcher angleMatcher = L_CODE_ANGLE_PATTERN.matcher(light);
            if (angleMatcher.matches()) {
                String baseCode = "L" + angleMatcher.group(1) + (angleMatcher.group(2) != null ? angleMatcher.group(2) : "");
                String angle = angleMatcher.group(3);
                lights.add(baseCode + "(" + angle + ")");
                log.debug("L-code com ângulo: {} -> {}", light, baseCode + "(" + angle + ")");
            } else {
                // Verificar se é um L-code simples
                Matcher simpleMatcher = L_CODE_SIMPLE_PATTERN.matcher(light);
                if (simpleMatcher.matches()) {
                    lights.add(light);
                    log.debug("L-code simples: {}", light);
                } else {
                    warnings.add(new ValidationWarning("luzes", light, "formato_invalido", 
                                                     ValidationWarning.Severity.WARNING, 
                                                     "Formato de luz não reconhecido: " + light));
                }
            }
        }
        
        return lights;
    }
    
    /**
     * Normaliza designadores de pista
     */
    public List<String> normalizeRunwayDesignators(String designatorsText, List<ValidationWarning> warnings) {
        List<String> designators = new ArrayList<>();
        
        if (designatorsText == null || designatorsText.trim().isEmpty()) {
            return designators;
        }
        
        String[] parts = designatorsText.split("[,\\s/]+");
        for (String part : parts) {
            String designator = part.trim();
            if (designator.isEmpty()) continue;
            
            Matcher matcher = RUNWAY_PATTERN.matcher(designator);
            if (matcher.matches()) {
                designators.add(designator);
            } else {
                warnings.add(new ValidationWarning("designadores_pista", designator, "formato_invalido", 
                                                 ValidationWarning.Severity.WARNING, 
                                                 "Designador de pista inválido: " + designator));
            }
        }
        
        return designators;
    }
    
    /**
     * Normaliza frequências
     */
    public List<Double> normalizeFrequencies(String frequenciesText, List<ValidationWarning> warnings) {
        List<Double> frequencies = new ArrayList<>();
        
        if (frequenciesText == null || frequenciesText.trim().isEmpty()) {
            return frequencies;
        }
        
        String[] parts = frequenciesText.split("[,\\s]+");
        for (String part : parts) {
            String freq = part.trim();
            if (freq.isEmpty()) continue;
            
            try {
                // Tentar converter diretamente
                double frequency = Double.parseDouble(freq);
                frequencies.add(frequency);
            } catch (NumberFormatException e) {
                // Tentar formato com ponto
                Matcher matcher = FREQUENCY_PATTERN.matcher(freq);
                if (matcher.matches()) {
                    try {
                        double frequency = Double.parseDouble(matcher.group(1) + "." + matcher.group(2));
                        frequencies.add(frequency);
                    } catch (NumberFormatException e2) {
                        warnings.add(new ValidationWarning("frequencias", freq, "conversao_falhou", 
                                                         ValidationWarning.Severity.ERROR, 
                                                         "Erro ao converter frequência: " + freq));
                    }
                } else {
                    warnings.add(new ValidationWarning("frequencias", freq, "formato_invalido", 
                                                     ValidationWarning.Severity.WARNING, 
                                                     "Formato de frequência não reconhecido: " + freq));
                }
            }
        }
        
        return frequencies;
    }
    
    /**
     * Normaliza INFOTEMP ID
     */
    public String normalizeInfotempId(String idText, List<ValidationWarning> warnings) {
        if (idText == null || idText.trim().isEmpty()) {
            return null;
        }
        
        String normalized = idText.trim().toUpperCase();
        Matcher matcher = INFOTEMP_ID_PATTERN.matcher(normalized);
        
        if (matcher.matches()) {
            return normalized;
        } else {
            warnings.add(new ValidationWarning("infotemp_id", idText, "formato_invalido", 
                                             ValidationWarning.Severity.WARNING, 
                                             "Formato INFOTEMP ID inválido. Use: [FRCM]NNNN/NNNN"));
            return idText; // Retornar original
        }
    }
    
    /**
     * Normaliza tipo de piso
     */
    public String normalizePiso(String pisoText, List<ValidationWarning> warnings) {
        if (pisoText == null || pisoText.trim().isEmpty()) {
            return null;
        }
        
        String normalized = pisoText.trim().toUpperCase();
        
        // Mapear abreviações comuns para códigos padrão
        switch (normalized) {
            case "ASF":
            case "ASPH":
            case "ASFALTO":
                return "ASPH";
            case "CONC":
            case "CONCRETO":
                return "CONC";
            case "TURF":
            case "GRAMA":
                return "TURF";
            case "GRAV":
            case "GRAVEL":
            case "CASCALHO":
                return "GRAV";
            case "TERR":
            case "TERRA":
                return "TERR";
            case "SOD":
                return "SOD";
            case "SNOW":
            case "NEVE":
                return "SNOW";
            case "ICE":
            case "GELO":
                return "ICE";
            default:
                warnings.add(new ValidationWarning("piso", pisoText, "tipo_nao_mapeado", 
                                                 ValidationWarning.Severity.INFO, 
                                                 "Tipo de piso não mapeado: " + pisoText));
                return normalized; // Retornar original
        }
    }
}
