package br.com.fplbr.pilot.aerodromos.infra.sol;

import br.com.fplbr.pilot.aerodromos.ports.in.SolServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SolServiceImpl implements SolServicePort {
    
    private static final Logger LOG = Logger.getLogger(SolServiceImpl.class);
    private static final int TIMEOUT_MS = 10_000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Inject
    @ConfigProperty(name = "sol.crawler.url", defaultValue = "https://www.timeanddate.com/sun/brazil/")
    String crawlerUrl;
    
    @Inject
    @ConfigProperty(name = "sol.crawler.enabled", defaultValue = "true")
    boolean crawlerEnabled;

    @Override
    public SolInfo obterSol(String icao, LocalDate data) {
        Objects.requireNonNull(icao, "Código ICAO não pode ser nulo");
        Objects.requireNonNull(data, "Data não pode ser nula");
        
        if (icao.trim().isEmpty() || icao.length() != 4) {
            throw new IllegalArgumentException("Código ICAO inválido: " + icao);
        }
        
        try {
            // Try to get from AISWEB crawler first if enabled
            if (crawlerEnabled) {
                try {
                    return obterDoCrawler(icao, data)
                            .orElseGet(() -> calcularAstronomicamente(icao, data));
                } catch (Exception e) {
                    LOG.errorf("Falha ao obter dados do crawler para %s em %s: %s", 
                            icao, data, e.getMessage(), e);
                }
            }
            
            // Fallback to astronomical calculation
            return calcularAstronomicamente(icao, data);
            
        } catch (Exception e) {
            LOG.errorf("Erro inesperado ao obter informações do sol para %s: %s", 
                    icao, e.getMessage(), e);
            // Return default values in case of any error
            return new SolInfo(data, 
                    data.getDayOfWeek().toString(), 
                    "06:00", "18:00");
        }
    }

    private Optional<SolInfo> obterDoCrawler(String icao, LocalDate data) throws IOException {
        if (!isValidUrl(crawlerUrl)) {
            LOG.warnf("URL do crawler inválida: %s", crawlerUrl);
            return Optional.empty();
        }
        
        try {
            String formattedDate = data.format(DATE_FORMATTER);
            String encodedIcao = URLEncoder.encode(icao, StandardCharsets.UTF_8);
            
            String url = String.format("%s?city=%s&month=%d&year=%d", 
                    crawlerUrl, 
                    encodedIcao,
                    data.getMonthValue(),
                    data.getYear());
            
            LOG.debugf("Buscando informações do sol em: %s", url);
            
            // Configure connection with timeouts
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .maxBodySize(0) // No limit
                    .followRedirects(true)
                    .validateTLSCertificates(true)
                    .get();
            
            // Sanitize and extract data
            doc.outputSettings().prettyPrint(false);
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            
            // Example selectors - adjust based on actual page structure
            String sunrise = safeSelect(doc, "#sunrise");
            String sunset = safeSelect(doc, "#sunset");
            
            if (sunrise == null || sunset == null) {
                LOG.warnf("Não foi possível extrair horários do sol da página: %s", url);
                return Optional.empty();
            }
            
            return Optional.of(new SolInfo(
                    data,
                    data.getDayOfWeek().toString(),
                    cleanTimeString(sunrise),
                    cleanTimeString(sunset)
            ));
            
        } catch (IOException e) {
            LOG.warnf("Falha ao acessar o crawler em %s: %s", crawlerUrl, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Erro inesperado ao processar crawler: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    private String safeSelect(Document doc, String selector) {
        try {
            return doc.selectFirst(selector) != null ? 
                   Jsoup.clean(doc.selectFirst(selector).text(), Safelist.none()) : 
                   null;
        } catch (Exception e) {
            LOG.debugf("Erro ao selecionar elemento %s: %s", selector, e.getMessage());
            return null;
        }
    }
    
    private String cleanTimeString(String time) {
        if (time == null) return "";
        // Remove any non-time characters, leaving only numbers and colons
        return time.replaceAll("[^0-9:]", "").trim();
    }
    
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            new URI(url).toURL();
            return true;
        } catch (URISyntaxException | IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            LOG.warnf("Erro ao validar URL %s: %s", url, e.getMessage());
            return false;
        }
    }
    
    private SolInfo calcularAstronomicamente(String icao, LocalDate data) {
        try {
            // In a real implementation, use a proper astronomical calculation library
            // This is a simplified placeholder
            double[] coords = obterCoordenadasAerodromo(icao);
            
            // Simple calculation based on date (equator reference)
            // In a real app, use proper astronomical formulas
            int dayOfYear = data.getDayOfYear();
            double lat = coords[0];
            
            // Very simplified calculation - not astronomically accurate
            // Just for demonstration - replace with proper calculations
            int sunriseHour = 6 + (int)(Math.sin(dayOfYear / 58.0) * 2);
            int sunsetHour = 18 - (int)(Math.sin(dayOfYear / 58.0) * 2);
            
            // Adjust for southern hemisphere
            if (lat < 0) {
                sunriseHour = 24 - sunsetHour;
                sunsetHour = 24 - sunriseHour;
            }
            
            String sunrise = String.format("%02d:00", Math.max(4, Math.min(8, sunriseHour)));
            String sunset = String.format("%02d:00", Math.min(20, Math.max(16, sunsetHour)));
            
            return new SolInfo(
                    data,
                    data.getDayOfWeek().toString(),
                    sunrise,
                    sunset
            );
            
        } catch (Exception e) {
            LOG.errorf("Erro no cálculo astronômico para %s: %s", icao, e.getMessage(), e);
            // Fallback to reasonable defaults
            return new SolInfo(data, data.getDayOfWeek().toString(), "06:00", "18:00");
        }
    }
    
    private double[] obterCoordenadasAerodromo(String icao) {
        // In a real implementation, this would query the aerodrome database
        // For now, return coordinates for SBSP (São Paulo/Guarulhos) as default
        // Consider adding a proper repository for aerodrome coordinates
        return new double[] {-23.4257, -46.4819};
    }
}
