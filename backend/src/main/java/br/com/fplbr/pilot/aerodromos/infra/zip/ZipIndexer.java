package br.com.fplbr.pilot.aerodromos.infra.zip;

import br.com.fplbr.pilot.aerodromos.dto.CartaAerodromoDTO;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApplicationScoped
public class ZipIndexer implements CartasIndexerPort {

    private static final Logger LOG = Logger.getLogger(ZipIndexer.class);
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PDF_EXTENSION = ".pdf";
    private static final String SHA_256 = "SHA-256";
    
    @Inject
    @ConfigProperty(name = "cartas.storage.dir", defaultValue = "data/cartas")
    String baseDir;

    private static final Pattern ICAO_PATTERN = Pattern.compile("([A-Z]{4})");
    private static final Pattern TIPO_PATTERN = Pattern.compile("(IAC|SID|STAR|VAC|ADC|VFR|IFR)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CICLO_PATTERN = Pattern.compile("(20\\d{2})(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])");

    @Override
    public List<CartaAerodromoDTO> indexarDeZip(byte[] zipBytes) {
        if (zipBytes == null || zipBytes.length == 0) {
            throw new IllegalArgumentException("Dados do ZIP não podem ser nulos ou vazios");
        }

        List<CartaAerodromoDTO> cartas = new ArrayList<>();
        
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    
                    String entryName = sanitizeFileName(entry.getName());
                    if (!entryName.toLowerCase().endsWith(PDF_EXTENSION)) {
                        LOG.debugf("Arquivo não PDF ignorado: %s", entryName);
                        continue;
                    }

                    String fileName = Paths.get(entryName).getFileName().toString();
                    String icao = extractIcao(fileName);
                    if (icao == null) {
                        LOG.warnf("Não foi possível extrair código ICAO do arquivo: %s", fileName);
                        continue;
                    }

                    byte[] fileContent = readEntryContent(zis, entry);
                    if (fileContent == null || fileContent.length == 0) {
                        LOG.warnf("Arquivo vazio ignorado: %s", fileName);
                        continue;
                    }

                    String fileHash = calculateHash(fileContent);
                    Path targetPath = saveFile(icao, fileName, fileContent);
                    
                    CartaAerodromoDTO carta = CartaAerodromoDTO.builder()
                        .icao(icao)
                        .titulo(removeExtension(fileName))
                        .tipo(extractTipo(fileName))
                        .caminho(targetPath.toString())
                        .hash(fileHash)
                        .ciclo(extractCiclo(fileName))
                        .build();
                    
                    cartas.add(carta);
                    
                } catch (Exception e) {
                    LOG.errorf("Erro ao processar entrada %s: %s", entry.getName(), e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao processar arquivo ZIP: " + e.getMessage(), e);
        }
        
        return Collections.unmodifiableList(cartas);
    }

    private String extractIcao(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = ICAO_PATTERN.matcher(fileName);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractTipo(String fileName) {
        if (fileName == null) {
            return "DESCONHECIDO";
        }
        Matcher matcher = TIPO_PATTERN.matcher(fileName);
        return matcher.find() ? matcher.group(1).toUpperCase() : "DESCONHECIDO";
    }

    private LocalDate extractCiclo(String fileName) {
        if (fileName == null) {
            return null;
        }
        Matcher matcher = CICLO_PATTERN.matcher(fileName);
        if (matcher.find()) {
            try {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));
                return LocalDate.of(year, month, day);
            } catch (DateTimeParseException | NumberFormatException e) {
                LOG.warnf("Formato de data inválido no nome do arquivo: %s", fileName);
            }
        }
        return null;
    }

    private String removeExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private String calculateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hashBytes = digest.digest(data);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash não disponível: " + SHA_256, e);
        }
    }

    private Path saveFile(String icao, String fileName, byte[] content) throws IOException {
        Path targetDir = Paths.get(baseDir, icao).normalize();
        
        // Security check to prevent directory traversal
        if (!targetDir.normalize().startsWith(Paths.get(baseDir).normalize())) {
            throw new SecurityException("Tentativa de acesso a diretório não autorizado: " + targetDir);
        }
        
        Files.createDirectories(targetDir);
        
        Path targetFile = targetDir.resolve(fileName).normalize();
        
        // Double check we're still within the allowed directory
        if (!targetFile.normalize().startsWith(targetDir.normalize())) {
            throw new SecurityException("Tentativa de acesso a arquivo não autorizado: " + targetFile);
        }
        
        if (!Files.exists(targetFile)) {
            Files.write(targetFile, content, StandardOpenOption.CREATE_NEW);
        }
        
        return targetFile;
    }
    
    private byte[] readEntryContent(ZipInputStream zis, ZipEntry entry) throws IOException {
        long size = entry.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IOException("Arquivo muito grande: " + entry.getName() + " (" + size + " bytes)");
        }
        
        // For entries with unknown size, read in chunks
        if (size <= 0) {
            return zis.readAllBytes();
        }
        
        // For entries with known size, read exactly that many bytes
        byte[] buffer = new byte[(int) size];
        int bytesRead = 0;
        int n;
        while (bytesRead < buffer.length && (n = zis.read(buffer, bytesRead, buffer.length - bytesRead)) != -1) {
            bytesRead += n;
        }
        return buffer;
    }
    
    private String sanitizeFileName(String name) {
        if (name == null) {
            return "";
        }
        // Remove path traversal attempts and normalize path
        return name.replaceAll("\\p{Cntrl}|[\\/:*?\"<>|]", "_");
    }
}
