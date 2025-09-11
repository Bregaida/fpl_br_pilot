package br.com.fplbr.pilot.aerodromos.infra.scheduler;

import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import io.quarkus.scheduler.Scheduled;
import br.com.fplbr.pilot.aerodromos.infra.http.RotaerDownloader;
import br.com.fplbr.pilot.aerodromos.infra.http.RotaerPdfParser;
import br.com.fplbr.pilot.aerodromos.application.service.AerodromoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class RotaerScheduler {
    private static final Logger log = LoggerFactory.getLogger(RotaerScheduler.class);
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);
    
    @Inject RotaerIngestionService ingestion;

    @PostConstruct
    void onStart() {
        atualizarDados();
    }

    @Scheduled(every = "240h") // a cada 10 dias
    void scheduledUpdate() {
        atualizarDados();
    }

    private void atualizarDados() {
        if (isUpdating.compareAndSet(false, true)) {
            try {
                log.info("Iniciando atualização de dados do ROTAER");
                ingestion.atualizarDados();
            } finally {
                isUpdating.set(false);
            }
        } else {
            log.warn("Atualização já em andamento, pulando execução");
        }
    }
}


