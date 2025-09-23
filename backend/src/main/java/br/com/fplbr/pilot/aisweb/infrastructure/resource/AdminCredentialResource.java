package br.com.fplbr.pilot.aisweb.infrastructure.resource;

import br.com.fplbr.pilot.aisweb.application.service.SecureCredentialService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/api/v1/admin/credentials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminCredentialResource {
    
    @ConfigProperty(name = "quarkus.profile", defaultValue = "dev")
    String profile;
    
    private static final Logger LOG = Logger.getLogger(AdminCredentialResource.class);
    
    @Inject
    SecureCredentialService credentialService;
    
    @POST
    @Path("/aisweb")
    public Response setAiswebCredentials(Map<String, String> credentials) {
        try {
            // Proteção: apenas em desenvolvimento
            if (!"dev".equals(profile)) {
                return Response.status(403)
                    .entity(Map.of("error", "Endpoint disponível apenas em desenvolvimento"))
                    .build();
            }
            
            String apiKey = credentials.get("apiKey");
            String apiPass = credentials.get("apiPass");
            
            if (apiKey == null || apiKey.trim().isEmpty() || 
                apiPass == null || apiPass.trim().isEmpty()) {
                return Response.status(400)
                    .entity(Map.of("error", "apiKey e apiPass são obrigatórios"))
                    .build();
            }
            
            credentialService.setAiswebCredentials(apiKey, apiPass);
            
            LOG.info("Credenciais do AISWEB configuradas com sucesso");
            
            return Response.ok(Map.of(
                "ok", true,
                "serviceName", "aisweb"
            )).build();
            
        } catch (Exception e) {
            LOG.error("Erro ao configurar credenciais do AISWEB", e);
            return Response.status(500)
                .entity(Map.of("error", "Erro interno do servidor"))
                .build();
        }
    }
    
    @GET
    @Path("/aisweb/status")
    public Response getCredentialStatus() {
        try {
            boolean hasCredentials = credentialService.hasCredentials();
            
            return Response.ok(Map.of(
                "hasCredentials", hasCredentials,
                "status", hasCredentials ? "configured" : "not_configured"
            )).build();
            
        } catch (Exception e) {
            LOG.error("Erro ao verificar status das credenciais", e);
            return Response.status(500)
                .entity(Map.of("error", "Erro interno do servidor"))
                .build();
        }
    }
}
