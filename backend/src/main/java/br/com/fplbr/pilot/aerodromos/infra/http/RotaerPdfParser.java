package br.com.fplbr.pilot.aerodromos.infra.http;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class RotaerPdfParser {
    private static final Pattern ICAO_LINE = Pattern.compile("\\b([A-Z]{4})\\b\\s*-\\s*(.+)");
    // Fallback: captura códigos ICAO brasileiros comuns (SB,SD,SN,SJ,SW,SS)
    private static final Pattern ICAO_FALLBACK = Pattern.compile("\\b(SB|SD|SN|SJ|SW|SS)[A-Z]{2}\\b");

    public List<Aerodromo> parse(byte[] pdfBytes) {
        List<Aerodromo> out = new ArrayList<>();
        if (pdfBytes == null || pdfBytes.length == 0) return out;
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            String[] lines = text.split("\r?\n");
            for (String line : lines) {
                Matcher m = ICAO_LINE.matcher(line.trim());
                if (m.find()) {
                    String icao = m.group(1);
                    String nome = m.group(2).trim();
                    out.add(Aerodromo.builder().icao(icao).nome(nome.isEmpty()?icao:nome).ativo(true).build());
                }
            }
            if (out.isEmpty()) {
                java.util.Set<String> codigos = new java.util.LinkedHashSet<>();
                Matcher m2 = ICAO_FALLBACK.matcher(text);
                while (m2.find()) {
                    codigos.add(m2.group());
                }
                for (String icao : codigos) {
                    out.add(Aerodromo.builder().icao(icao).nome(icao).ativo(true).build());
                }
            }
        } catch (Exception ignore) {}
        return out;
    }
}


