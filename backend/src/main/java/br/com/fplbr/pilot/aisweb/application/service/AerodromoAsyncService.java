package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.dto.AerodromoIcaoIataDto;
import br.com.fplbr.pilot.aisweb.infrastructure.persistence.entity.AerodromoIcaoIataEntity;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AerodromoAsyncService {
    
    private static final Logger LOG = Logger.getLogger(AerodromoAsyncService.class.getName());
    
    @Inject
    AerodromoIcaoIataService aerodromoService;
    
    @Inject
    AerodromoQueueService queueService;
    
    /**
     * Consulta API AISWEB e adiciona aeródromos na fila para processamento assíncrono
     * Retorna resposta imediata sem aguardar o processamento
     */
    public String consultarEAdicionarNaFila() {
        try {
            LOG.info("🔄 Iniciando consulta à API AISWEB (modo assíncrono)...");
            
            // Consultar API AISWEB (reutilizando método existente)
            String xmlResponse = aerodromoService.consultarApiAisweb();
            LOG.info("📡 Resposta da API recebida: " + (xmlResponse != null ? xmlResponse.length() + " caracteres" : "null"));
            
            // Parsear XML e extrair dados
            List<AerodromoIcaoIataEntity> aerodromos = aerodromoService.parsearXmlResponse(xmlResponse);
            LOG.info("📋 Aeródromos parseados: " + aerodromos.size());
            
            // Adicionar na fila Redis para processamento assíncrono
            queueService.adicionarAerodromosNaFila(aerodromos);
            
            String mensagem = String.format(
                "✅ %d aeródromos adicionados na fila para processamento assíncrono. " +
                "O processamento será feito em lotes de 50 aeródromos a cada 5 segundos.", 
                aerodromos.size()
            );
            
            LOG.info(mensagem);
            return mensagem;
                    
        } catch (Exception e) {
            String erro = "❌ Erro ao consultar API e adicionar na fila: " + e.getMessage();
            LOG.severe(erro);
            e.printStackTrace();
            throw new RuntimeException(erro, e);
        }
    }
    
    /**
     * Obtém o status atual do processamento
     */
    public String obterStatusProcessamento() {
        return queueService.obterStatusProcessamento();
    }
    
    /**
     * Limpa a fila (útil para desenvolvimento)
     */
    public void limparFila() {
        queueService.limparFila();
    }
}
