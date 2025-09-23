package br.com.fplbr.pilot.common.infrastructure.web.resource;

import br.com.fplbr.pilot.common.application.service.CepService;
import br.com.fplbr.pilot.common.infrastructure.web.dto.CepResponseDto;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/api/v1/cep")
@Tag(name = "Usuário", description = "APIs relacionadas ao usuário")
public class CepResource {
    
    private static final Logger LOG = Logger.getLogger(CepResource.class);
    
    @Inject
    CepService cepService;
    
    @GET
    @Path("/{cep}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Buscar endereço por CEP",
        description = "Busca informações de endereço (logradouro, bairro, cidade, UF) através do CEP. " +
                     "Utiliza ViaCEP como fonte principal e República Virtual como fallback."
    )
    @APIResponse(
        responseCode = "200",
        description = "Endereço encontrado com sucesso",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CepResponseDto.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "CEP inválido",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CepResponseDto.class)
        )
    )
    public CepResponseDto buscarCep(@PathParam("cep") String cep) {
        LOG.info("CEP Resource: Buscando CEP: " + cep);
        try {
            CepResponseDto result = cepService.buscarCep(cep);
            LOG.info("CEP Resource: Resultado: " + result);
            return result;
        } catch (Exception e) {
            LOG.error("CEP Resource: Erro ao buscar CEP: " + e.getMessage(), e);
            return CepResponseDto.builder()
                .erro(true)
                .mensagem("Erro interno: " + e.getMessage())
                .build();
        }
    }
}
