package br.com.fplbr.pilot.aerodromos.infra.scheduler;

import br.com.fplbr.pilot.aerodromos.application.service.AerodromoService;
import br.com.fplbr.pilot.aerodromos.infra.http.RotaerDownloader;
import br.com.fplbr.pilot.aerodromos.infra.http.RotaerPdfParser;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RotaerIngestionService {
    @Inject CartasZipDownloaderPort downloader;
    @Inject CartasIndexerPort indexer;
    @Inject CartaRepositoryPort cartaRepo;
    @Inject RotaerDownloader rotaerDownloader;
    @Inject RotaerPdfParser rotaerParser;
    @Inject AerodromoService aerodromoService;

    public IngestionResult atualizarDados() {
        IngestionResult result = new IngestionResult();

        // ROTAER PDF → Aeródromos
        try {
            byte[] pdf = rotaerDownloader.baixarPdf();
            result.pdfDownloaded = pdf != null && pdf.length > 0;
            var aerodromos = rotaerParser.parse(pdf);
            result.aerodromosParsed = aerodromos != null ? aerodromos.size() : 0;
            if (result.aerodromosParsed == 0) {
                // Fallback mínimo para não ficar vazio (seed inicial)
                aerodromos = java.util.List.of(
                        br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo.builder().icao("SBSP").nome("CONGONHAS").ativo(true).build(),
                        br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo.builder().icao("SBMT").nome("CAMPO DE MARTE").ativo(true).build()
                );
            }
            int persisted = 0;
            for (var a : aerodromos) {
                try { aerodromoService.salvarAerodromo(a); persisted++; } catch (Exception ignored) {}
            }
            result.aerodromosPersisted = persisted;
        } catch (Exception e) {
            result.message = (result.message == null ? "" : result.message + " | ") + "erro ROTAER: " + e.getClass().getSimpleName();
        }

        // ZIP de Cartas → Repositório
        try {
            byte[] zip = downloader.baixarZip();
            result.zipDownloaded = zip != null && zip.length > 0;
            if (result.zipDownloaded) {
                var cartas = indexer.indexarDeZip(zip);
                if (!cartas.isEmpty()) { cartaRepo.upsertAll(cartas); }
                result.cartasIndexed = cartas.size();
            }
        } catch (Exception e) {
            result.message = (result.message == null ? "" : result.message + " | ") + "erro CARTAS: " + e.getClass().getSimpleName();
        }

        return result;
    }

    public static class IngestionResult {
        public boolean pdfDownloaded;
        public int aerodromosParsed;
        public int aerodromosPersisted;
        public boolean zipDownloaded;
        public int cartasIndexed;
        public String message;
    }
}


