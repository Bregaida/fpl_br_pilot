package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.dto.AerodromoIcaoIataDto;
import br.com.fplbr.pilot.aisweb.infrastructure.persistence.entity.AerodromoIcaoIataEntity;
import br.com.fplbr.pilot.aisweb.infrastructure.persistence.repository.AerodromoIcaoIataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AerodromoQueueService {
    
    private static final Logger LOG = Logger.getLogger(AerodromoQueueService.class.getName());
    private static final String QUEUE_NAME = "aerodromos_queue";
    private static final String PROCESSING_STATUS_KEY = "aerodromos_processing_status";
    
    @Inject
    RedisDataSource redisDataSource;
    
    @Inject
    AerodromoIcaoIataRepository repository;
    
    @Inject
    ObjectMapper objectMapper;
    
    @ConfigProperty(name = "aerodromo.queue.batch.size", defaultValue = "50")
    int batchSize;
    
    private ListCommands<String, String> listCommands;
    
    public void init() {
        if (this.listCommands == null) {
            this.listCommands = redisDataSource.list(String.class, String.class);
        }
    }
    
    /**
     * Adiciona aeródromos na fila para processamento assíncrono
     */
    public void adicionarAerodromosNaFila(List<AerodromoIcaoIataEntity> aerodromos) {
        init();
        
        try {
            LOG.info("📦 Adicionando " + aerodromos.size() + " aeródromos na fila Redis");
            
            for (AerodromoIcaoIataEntity aerodromo : aerodromos) {
                String json = objectMapper.writeValueAsString(aerodromo);
                listCommands.lpush(QUEUE_NAME, json);
            }
            
            // Atualizar status do processamento
            redisDataSource.value(String.class).set(PROCESSING_STATUS_KEY, 
                "QUEUED:" + aerodromos.size() + ":" + LocalDateTime.now());
            
            LOG.info("✅ Aeródromos adicionados na fila com sucesso");
            
        } catch (Exception e) {
            LOG.severe("❌ Erro ao adicionar aeródromos na fila: " + e.getMessage());
            throw new RuntimeException("Erro ao adicionar aeródromos na fila", e);
        }
    }
    
    /**
     * Processa lotes de aeródromos da fila a cada 5 segundos
     */
    @Scheduled(every = "5s")
    public void processarLoteDaFila() {
        init();
        
        try {
            // Verificar se há itens na fila
            long tamanhoFila = listCommands.llen(QUEUE_NAME);
            if (tamanhoFila == 0) {
                return; // Nada para processar
            }
            
            LOG.info("📋 Fila tem " + tamanhoFila + " aeródromos. Processando lote de " + batchSize);
            
            // Buscar um lote da fila
            List<AerodromoIcaoIataEntity> lote = buscarLoteDaFila();
            
            if (!lote.isEmpty()) {
                // Processar lote em transação
                processarLoteAerodromos(lote);
                
                // Atualizar status
                long restante = listCommands.llen(QUEUE_NAME);
                redisDataSource.value(String.class).set(PROCESSING_STATUS_KEY, 
                    "PROCESSING:" + lote.size() + ":" + restante + ":" + LocalDateTime.now());
                
                LOG.info("✅ Lote de " + lote.size() + " aeródromos processado. Restam: " + restante);
            }
            
        } catch (Exception e) {
            LOG.severe("❌ Erro ao processar lote da fila: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Busca um lote de aeródromos da fila Redis
     */
    private List<AerodromoIcaoIataEntity> buscarLoteDaFila() {
        List<AerodromoIcaoIataEntity> lote = new ArrayList<>();
        
        try {
            for (int i = 0; i < batchSize; i++) {
                String json = listCommands.rpop(QUEUE_NAME);
                if (json == null) break; // Fila vazia
                
                AerodromoIcaoIataEntity aerodromo = objectMapper.readValue(json, AerodromoIcaoIataEntity.class);
                lote.add(aerodromo);
            }
        } catch (Exception e) {
            LOG.severe("❌ Erro ao buscar lote da fila: " + e.getMessage());
        }
        
        return lote;
    }
    
    /**
     * Processa um lote de aeródromos em uma transação
     */
    @Transactional
    public void processarLoteAerodromos(List<AerodromoIcaoIataEntity> lote) {
        for (AerodromoIcaoIataEntity aerodromo : lote) {
            try {
                LOG.info("💾 Processando aeródromo: " + aerodromo.getIcao() + " - " + aerodromo.getNomeAerodromo());
                salvarOuAtualizarAerodromo(aerodromo);
            } catch (Exception e) {
                LOG.severe("❌ Erro ao salvar aeródromo " + aerodromo.getIcao() + ": " + e.getMessage());
                // Continuar com o próximo aeródromo mesmo se um falhar
            }
        }
    }
    
    /**
     * Salva ou atualiza um aeródromo no banco
     */
    private AerodromoIcaoIataEntity salvarOuAtualizarAerodromo(AerodromoIcaoIataEntity aerodromo) {
        try {
            // Tentar buscar aeródromo existente
            AerodromoIcaoIataEntity existente = repository.findByIcao(aerodromo.getIcao());
            
            if (existente != null) {
                // Atualizar aeródromo existente
                existente.setCiadId(aerodromo.getCiadId());
                existente.setCiad(aerodromo.getCiad());
                existente.setTipoAerodromo(aerodromo.getTipoAerodromo());
                existente.setIata(aerodromo.getIata());
                existente.setNomeAerodromo(aerodromo.getNomeAerodromo());
                existente.setCidadeAerodromo(aerodromo.getCidadeAerodromo());
                existente.setUfAerodromo(aerodromo.getUfAerodromo());
                existente.setDataPublicacao(aerodromo.getDataPublicacao());
                existente.setDataAtualizacao(LocalDateTime.now());
                
                return repository.getEntityManager().merge(existente);
            } else {
                // Salvar novo aeródromo
                aerodromo.setDataAtualizacao(LocalDateTime.now());
                repository.persist(aerodromo);
                return aerodromo;
            }
        } catch (Exception e) {
            LOG.severe("❌ Erro ao salvar/atualizar aeródromo " + aerodromo.getIcao() + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtém o status atual do processamento
     */
    public String obterStatusProcessamento() {
        try {
            init();
            
            String status = redisDataSource.value(String.class).get(PROCESSING_STATUS_KEY);
            long tamanhoFila = listCommands.llen(QUEUE_NAME);
            
            return String.format("Status: %s | Fila: %d itens", 
                status != null ? status : "IDLE", tamanhoFila);
                
        } catch (Exception e) {
            return "Erro ao obter status: " + e.getMessage();
        }
    }
    
    /**
     * Limpa a fila (útil para desenvolvimento)
     */
    public void limparFila() {
        init();
        
        try {
            // Limpar a lista (fila)
            long tamanhoAntes = listCommands.llen(QUEUE_NAME);
            listCommands.ltrim(QUEUE_NAME, 1, 0); // Remove todos os itens
            
            // Limpar status
            redisDataSource.value(String.class).set(PROCESSING_STATUS_KEY, "CLEARED");
            
            LOG.info("🧹 Fila limpa: " + tamanhoAntes + " itens removidos");
        } catch (Exception e) {
            LOG.severe("❌ Erro ao limpar fila: " + e.getMessage());
        }
    }
}
