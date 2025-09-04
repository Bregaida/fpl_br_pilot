package br.com.fplbr.pilot.aerodromos.infra.scheduler;

import br.com.fplbr.pilot.aerodromos.ports.in.CartasIndexerPort;
import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class AtualizadorCartasScheduler {

    private static final Logger LOG = Logger.getLogger(AtualizadorCartasScheduler.class);
    private static final String LOCK_NAME = "atualizacao-cartas-lock";
    
    private final CartasZipDownloaderPort zipDownloader;
    private final CartasIndexerPort indexer;
    private final CartaRepositoryPort repository;
    private final Event<AtualizacaoCartasEvent> eventPublisher;
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicReference<Instant> lastRun = new AtomicReference<>();
    private final AtomicReference<Instant> lastSuccess = new AtomicReference<>();
    
    @Inject
    @ConfigProperty(name = "scheduler.cartas.enabled", defaultValue = "true")
    boolean schedulerEnabled;
    
    @Inject
    @ConfigProperty(name = "scheduler.cartas.timeout.minutes", defaultValue = "30")
    long timeoutMinutes;

    public AtualizadorCartasScheduler(CartasZipDownloaderPort zipDownloader, 
                                     CartasIndexerPort indexer, 
                                     CartaRepositoryPort repository,
                                     Event<AtualizacaoCartasEvent> eventPublisher) {
        this.zipDownloader = zipDownloader;
        this.indexer = indexer;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(identity = "atualizacao-cartas", 
               cron = "{scheduler.cartas.cron:0 0 2 ? * MON}", // Every Monday at 2 AM
               skipExecutionIf = SchedulerSkipPredicate.class)
    public void atualizarCartas() {
        if (!schedulerEnabled) {
            LOG.info("Agendador de atualização de cartas está desativado");
            return;
        }
        
        if (!isRunning.compareAndSet(false, true)) {
            LOG.warn("Atualização de cartas já em andamento, pulando esta execução");
            return;
        }
        
        final Instant startTime = Instant.now();
        lastRun.set(startTime);
        boolean success = false;
        int cartasProcessadas = 0;
        
        try {
            LOG.info("Iniciando atualização de cartas aeronáuticas");
            
            // Download the ZIP file
            LOG.info("Baixando arquivo ZIP de cartas...");
            byte[] zipBytes = zipDownloader.baixarZip();
            
            if (zipBytes == null || zipBytes.length == 0) {
                throw new IllegalStateException("Arquivo ZIP vazio ou inválido");
            }
            
            LOG.infof("Download concluído. Tamanho: %,d bytes", zipBytes.length);
            
            // Index and process the charts
            LOG.info("Processando e indexando cartas...");
            var cartas = indexer.indexarDeZip(zipBytes);
            cartasProcessadas = cartas.size();
            
            if (cartas.isEmpty()) {
                LOG.warn("Nenhuma carta válida encontrada no arquivo ZIP");
            } else {
                // Save to database
                LOG.infof("Salvando %,d cartas no banco de dados...", cartas.size());
                repository.upsertAll(cartas);
                
                lastSuccess.set(Instant.now());
                success = true;
                
                // Publish success event
                eventPublisher.fireAsync(new AtualizacaoCartasEvent(
                    true, 
                    cartasProcessadas, 
                    startTime, 
                    Duration.between(startTime, Instant.now())
                ));
                
                LOG.infof("Atualização de cartas concluída com sucesso. %d cartas processadas em %s", 
                        cartasProcessadas, 
                        formatDuration(Duration.between(startTime, Instant.now())));
            }
            
        } catch (Exception e) {
            LOG.error("Falha ao atualizar cartas aeronáuticas: " + e.getMessage(), e);
            
            // Publish failure event
            eventPublisher.fireAsync(new AtualizacaoCartasEvent(
                false, 
                cartasProcessadas, 
                startTime, 
                Duration.between(startTime, Instant.now()),
                e
            ));
            
            throw new RuntimeException("Falha na atualização de cartas", e);
        } finally {
            isRunning.set(false);
        }
    }
    
    public boolean isRunning() {
        return isRunning.get();
    }
    
    public Instant getLastRun() {
        return lastRun.get();
    }
    
    public Instant getLastSuccess() {
        return lastSuccess.get();
    }
    
    private String formatDuration(Duration duration) {
        return String.format("%d min, %d sec", 
                duration.toMinutes(), 
                duration.minusMinutes(duration.toMinutes()).getSeconds());
    }
    
    public static class SchedulerSkipPredicate implements jakarta.enterprise.util.Nonbinding {
        boolean test(AtualizadorCartasScheduler scheduler) {
            return scheduler.isRunning() || 
                   (scheduler.getLastRun() != null && 
                    Duration.between(scheduler.getLastRun(), Instant.now()).toMinutes() < 60);
        }
    }
    
    public static class AtualizacaoCartasEvent {
        private final boolean success;
        private final int cartasProcessadas;
        private final Instant timestamp;
        private final Duration duracao;
        private final Throwable error;
        
        public AtualizacaoCartasEvent(boolean success, int cartasProcessadas, 
                                    Instant timestamp, Duration duracao) {
            this(success, cartasProcessadas, timestamp, duracao, null);
        }
        
        public AtualizacaoCartasEvent(boolean success, int cartasProcessadas, 
                                    Instant timestamp, Duration duracao, Throwable error) {
            this.success = success;
            this.cartasProcessadas = cartasProcessadas;
            this.timestamp = timestamp;
            this.duracao = duracao;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public int getCartasProcessadas() { return cartasProcessadas; }
        public Instant getTimestamp() { return timestamp; }
        public Duration getDuracao() { return duracao; }
        public Throwable getError() { return error; }
    }
}
