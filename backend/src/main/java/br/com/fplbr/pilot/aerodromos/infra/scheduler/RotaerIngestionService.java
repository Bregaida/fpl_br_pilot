package br.com.fplbr.pilot.aerodromos.infra.scheduler;

import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import br.com.fplbr.pilot.rotaer.application.service.RotaerIntegrationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RotaerIngestionService {
    @Inject CartasZipDownloaderPort downloader;
    @Inject CartasIndexerPort indexer;
    @Inject CartaRepositoryPort cartaRepo;
    @Inject RotaerIntegrationService rotaerIntegrationService;

    public IngestionResult atualizarDados() {
        IngestionResult result = new IngestionResult();

        // ROTAER PDF → Aeródromos (usando novo pipeline)
        try {
            RotaerIntegrationService.IngestionResult rotaerResult = rotaerIntegrationService.atualizarDados();
            
            // Mapear resultado do novo serviço para o formato existente
            result.pdfDownloaded = rotaerResult.pdfDownloaded;
            result.aerodromosParsed = rotaerResult.aerodromosParsed;
            result.aerodromosPersisted = rotaerResult.aerodromosPersisted;
            
            if (rotaerResult.message != null && !rotaerResult.message.isEmpty()) {
                result.message = "ROTAER: " + rotaerResult.message;
            }
            
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


