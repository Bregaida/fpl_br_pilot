package br.com.fplbr.pilot.rotaer.application.service;

import br.com.fplbr.pilot.rotaer.domain.model.RotaerData;
import br.com.fplbr.pilot.rotaer.domain.model.ValidationWarning;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para RotaerNormalizer
 */
@QuarkusTest
public class RotaerNormalizerTest {
    
    @Inject
    RotaerNormalizer normalizer;
    
    @Test
    public void testNormalizeCoordinates_StandardFormat() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        RotaerData.Coordenadas coords = normalizer.normalizeCoordinates(
            "22 48 36S / 043 15 02W", warnings);
        
        assertNotNull(coords);
        assertEquals("22 48 36S", coords.getLatDms());
        assertEquals("043 15 02W", coords.getLonDms());
        assertEquals(-22.81, coords.getLatDd(), 0.01);
        assertEquals(-43.250556, coords.getLonDd(), 0.000001);
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeCoordinates_CompactFormat() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        RotaerData.Coordenadas coords = normalizer.normalizeCoordinates(
            "2249.77S/04314.20W", warnings);
        
        assertNotNull(coords);
        assertEquals("22 49 46.20S", coords.getLatDms());
        assertEquals("043 14 12.00W", coords.getLonDms());
        assertEquals(-22.8295, coords.getLatDd(), 0.0001);
        assertEquals(-43.2367, coords.getLonDd(), 0.0001);
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeCoordinates_InvalidFormat() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        RotaerData.Coordenadas coords = normalizer.normalizeCoordinates(
            "coordenadas inválidas", warnings);
        
        assertNull(coords);
        assertFalse(warnings.isEmpty());
        assertEquals("formato_invalido", warnings.get(0).getRegra());
    }
    
    @Test
    public void testNormalizeDimensions() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        RotaerData.DimensoesPista dimensoes = normalizer.normalizeDimensions(
            "3180x47", warnings);
        
        assertNotNull(dimensoes);
        assertEquals(3180, dimensoes.getComprimentoM());
        assertEquals(47, dimensoes.getLarguraM());
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizePcn() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        String pcn = normalizer.normalizePcn("73/F/B/X/T", warnings);
        
        assertEquals("73/F/B/X/T", pcn);
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeLights() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        List<String> lights = normalizer.normalizeLights("L21,L23,L26", warnings);
        
        assertNotNull(lights);
        assertEquals(3, lights.size());
        assertTrue(lights.contains("L21"));
        assertTrue(lights.contains("L23"));
        assertTrue(lights.contains("L26"));
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeLightsWithAngle() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        List<String> lights = normalizer.normalizeLights("L9(2.95),L14A", warnings);
        
        assertNotNull(lights);
        assertEquals(2, lights.size());
        assertTrue(lights.contains("L9(2.95)"));
        assertTrue(lights.contains("L14A"));
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeRunwayDesignators() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        List<String> designators = normalizer.normalizeRunwayDesignators("15/33", warnings);
        
        assertNotNull(designators);
        assertEquals(2, designators.size());
        assertTrue(designators.contains("15"));
        assertTrue(designators.contains("33"));
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeFrequencies() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        List<Double> frequencies = normalizer.normalizeFrequencies("118.000,121.500", warnings);
        
        assertNotNull(frequencies);
        assertEquals(2, frequencies.size());
        assertEquals(118.000, frequencies.get(0));
        assertEquals(121.500, frequencies.get(1));
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizeInfotempId() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        String id = normalizer.normalizeInfotempId("F0838/2018", warnings);
        
        assertEquals("F0838/2018", id);
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    public void testNormalizePiso() {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        String piso = normalizer.normalizePiso("ASF", warnings);
        assertEquals("ASPH", piso);
        
        piso = normalizer.normalizePiso("CONC", warnings);
        assertEquals("CONC", piso);
        
        piso = normalizer.normalizePiso("TURF", warnings);
        assertEquals("TURF", piso);
        
        assertTrue(warnings.isEmpty());
    }
}
