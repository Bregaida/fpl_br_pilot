package br.com.fplbr.pilot.aerodromos.infra.zip;

import br.com.fplbr.pilot.aerodromos.application.dto.CartaAerodromoDTO;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApplicationScoped
public class CartasIndexerAdapter implements CartasIndexerPort {
    @Override
    public List<CartaAerodromoDTO> indexarDeZip(byte[] zipBytes) {
        List<CartaAerodromoDTO> list = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                // Heurística simples: caminhos do tipo SBSP/VAC_SBSP_xxx.pdf
                String upper = name.toUpperCase();
                if (!upper.endsWith(".PDF")) continue;
                String icao = null; String tipo = null; String titulo = name;
                // Extrair ICAO pelo primeiro segmento de pasta ou pelo nome
                String[] parts = upper.split("/");
                if (parts.length > 1 && parts[0].matches("[A-Z]{4}")) {
                    icao = parts[0];
                }
                if (icao == null) {
                    var m = java.util.regex.Pattern.compile("([A-Z]{4})").matcher(upper);
                    if (m.find()) icao = m.group(1);
                }
                // Tipo pela sigla no nome
                if (upper.contains("VAC")) tipo = "VAC";
                else if (upper.contains("IAC")) tipo = "IAC";
                else if (upper.contains("SID")) tipo = "SID";
                else if (upper.contains("STAR")) tipo = "STAR";
                else tipo = "ROTA";

                if (icao != null) {
                    list.add(CartaAerodromoDTO.builder()
                            .icao(icao)
                            .tipo(tipo)
                            .titulo(titulo)
                            .href("/api/v1/aerodromos/cartas/" + icao + "/" + titulo.replace(' ', '_'))
                            .dofValidoDe(LocalDate.now())
                            .dofValidoAte(LocalDate.now().plusDays(28))
                            .build());
                }
            }
        } catch (Exception ignore) {}
        return list;
    }
}


