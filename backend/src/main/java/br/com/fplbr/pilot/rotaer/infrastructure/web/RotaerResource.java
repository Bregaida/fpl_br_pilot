package br.com.fplbr.pilot.rotaer.infrastructure.web;

import br.com.fplbr.pilot.rotaer.application.service.RotaerService;
import br.com.fplbr.pilot.rotaer.domain.model.RotaerProcessResult;
import br.com.fplbr.pilot.rotaer.domain.model.ValidationWarning;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Endpoint REST para ingestão de dados do ROTAER
 */
@Path("/api/v1/rotaer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RotaerResource {
    
    private static final Logger log = LoggerFactory.getLogger(RotaerResource.class);
    
    @Inject
    RotaerService rotaerService;
    
    /**
     * Ingestão de dados do ROTAER
     * POST /api/v1/rotaer/ingest?dryRun=true|false
     */
    @POST
    @Path("/ingest")
    public Response ingestRotaer(
            @QueryParam("dryRun") @DefaultValue("false") boolean dryRun,
            RotaerIngestRequest request) {
        
        log.info("Recebida requisição de ingestão ROTAER - ICAO: {}, dryRun: {}", 
                request != null ? request.getIcao() : "null", dryRun);
        
        try {
            // Validar entrada
            if (request == null) {
                log.error("Request é null");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Request é obrigatório"))
                    .build();
            }
            
            if (request.getIcao() == null || request.getIcao().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "ICAO é obrigatório"))
                    .build();
            }
            
            if (request.getConteudo() == null || request.getConteudo().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Conteúdo do ROTAER é obrigatório"))
                    .build();
            }
            
            // Processar
            log.debug("Iniciando processamento para ICAO: {}", request.getIcao().trim().toUpperCase());
            RotaerProcessResult result = rotaerService.processRotaer(
                request.getIcao().trim().toUpperCase(), 
                request.getConteudo(), 
                dryRun
            );
            log.debug("Processamento concluído para ICAO: {} - Resultado: {}", request.getIcao().trim().toUpperCase(), result.isOk());
            
            // Preparar resposta
            Map<String, Object> response = Map.of(
                "ok", result.isOk(),
                "icao", result.getIcao(),
                "dryRun", result.isDryRun(),
                "message", result.getMessage(),
                "warnings", result.getWarnings(),
                "json", result.getJson(),
                "diff", result.getDiff()
            );
            
            // Para dry-run, sempre retornar 200, mesmo com warnings
            Response.Status status = dryRun ? Response.Status.OK : 
                                   (result.isOk() ? Response.Status.OK : Response.Status.BAD_REQUEST);
            
            log.info("Ingestão ROTAER concluída - ICAO: {}, sucesso: {}, warnings: {}", 
                    result.getIcao(), result.isOk(), result.getWarnings().size());
            
            return Response.status(status).entity(response).build();
            
        } catch (Exception e) {
            log.error("Erro inesperado no processamento ROTAER para ICAO: {}", 
                     request != null ? request.getIcao() : "null", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("erro", "Erro interno: " + e.getMessage()))
                .build();
        }
    }
    
    /**
     * Ingestão em lote
     * POST /api/v1/rotaer/ingest/batch?dryRun=true|false
     */
    @POST
    @Path("/ingest/batch")
    public Response ingestBatch(
            @QueryParam("dryRun") @DefaultValue("false") boolean dryRun,
            RotaerBatchRequest request) {
        
        log.info("Recebida requisição de ingestão em lote - {} aeródromos, dryRun: {}", 
                request.getAerodromos().size(), dryRun);
        
        try {
            // Validar entrada
            if (request.getAerodromos() == null || request.getAerodromos().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Lista de aeródromos é obrigatória"))
                    .build();
            }
            
            // Processar em lote
            List<RotaerProcessResult> results = rotaerService.processBatch(
                request.getAerodromos(), 
                dryRun
            );
            
            // Estatísticas
            long successCount = results.stream().mapToLong(r -> r.isOk() ? 1 : 0).sum();
            long totalWarnings = results.stream().mapToLong(r -> r.getWarnings().size()).sum();
            
            Map<String, Object> response = Map.of(
                "ok", true,
                "dryRun", dryRun,
                "total", results.size(),
                "sucessos", successCount,
                "falhas", results.size() - successCount,
                "warnings_totais", totalWarnings,
                "resultados", results
            );
            
            log.info("Ingestão em lote concluída - {}/{} sucessos, {} warnings", 
                    successCount, results.size(), totalWarnings);
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            log.error("Erro durante ingestão em lote", e);
            
            Map<String, Object> errorResponse = Map.of(
                "ok", false,
                "erro", "Erro interno: " + e.getMessage(),
                "dryRun", dryRun
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
        }
    }
    
    /**
     * Validação apenas (sem persistência)
     * POST /api/v1/rotaer/validate
     */
    @POST
    @Path("/validate")
    public Response validateRotaer(RotaerIngestRequest request) {
        
        log.info("Recebida requisição de validação ROTAER - ICAO: {}", 
                request != null ? request.getIcao() : "null");
        
        try {
            // Validar entrada
            if (request == null) {
                log.error("Request é null");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Request é obrigatório"))
                    .build();
            }
            
            if (request.getIcao() == null || request.getIcao().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "ICAO é obrigatório"))
                    .build();
            }
            
            if (request.getConteudo() == null || request.getConteudo().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Conteúdo do ROTAER é obrigatório"))
                    .build();
            }
            
            // Validar
            List<ValidationWarning> warnings = rotaerService.validateOnly(
                request.getIcao().trim().toUpperCase(), 
                request.getConteudo()
            );
            
            // Contar por severidade
            long errorCount = warnings.stream().mapToLong(w -> 
                w.getSeveridade() == ValidationWarning.Severity.ERROR ? 1 : 0).sum();
            long warningCount = warnings.stream().mapToLong(w -> 
                w.getSeveridade() == ValidationWarning.Severity.WARNING ? 1 : 0).sum();
            long infoCount = warnings.stream().mapToLong(w -> 
                w.getSeveridade() == ValidationWarning.Severity.INFO ? 1 : 0).sum();
            
            Map<String, Object> response = Map.of(
                "ok", errorCount == 0,
                "icao", request.getIcao(),
                "warnings", warnings,
                "estatisticas", Map.of(
                    "total", warnings.size(),
                    "erros", errorCount,
                    "avisos", warningCount,
                    "informacoes", infoCount
                )
            );
            
            log.info("Validação ROTAER concluída - ICAO: {}, warnings: {} (erros: {})", 
                    request.getIcao(), warnings.size(), errorCount);
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            log.error("Erro durante validação ROTAER", e);
            
            Map<String, Object> errorResponse = Map.of(
                "ok", false,
                "erro", "Erro interno: " + e.getMessage(),
                "icao", request.getIcao()
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
        }
    }
    
    /**
     * Estatísticas de processamento
     * GET /api/v1/rotaer/stats
     */
    @GET
    @Path("/stats")
    public Response getStats() {
        
        log.debug("Recebida requisição de estatísticas ROTAER");
        
        try {
            Map<String, Object> stats = rotaerService.getProcessingStats();
            
            return Response.ok(stats).build();
            
        } catch (Exception e) {
            log.error("Erro ao obter estatísticas", e);
            
            Map<String, Object> errorResponse = Map.of(
                "erro", "Erro interno: " + e.getMessage()
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
        }
    }
    
    /**
     * Health check
     * GET /api/v1/rotaer/health
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of(
            "status", "UP",
            "service", "ROTAER",
            "timestamp", java.time.OffsetDateTime.now()
        )).build();
    }
    
    /**
     * Request DTO para ingestão individual
     */
    public static class RotaerIngestRequest {
        private String icao;
        private String conteudo;
        
        public RotaerIngestRequest() {}
        
        public RotaerIngestRequest(String icao, String conteudo) {
            this.icao = icao;
            this.conteudo = conteudo;
        }
        
        public String getIcao() { return icao; }
        public void setIcao(String icao) { this.icao = icao; }
        
        public String getConteudo() { return conteudo; }
        public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    }
    
    /**
     * Request DTO para ingestão em lote
     */
    public static class RotaerBatchRequest {
        private Map<String, String> aerodromos;
        
        public RotaerBatchRequest() {}
        
        public RotaerBatchRequest(Map<String, String> aerodromos) {
            this.aerodromos = aerodromos;
        }
        
        public Map<String, String> getAerodromos() { return aerodromos; }
        public void setAerodromos(Map<String, String> aerodromos) { this.aerodromos = aerodromos; }
    }
}
