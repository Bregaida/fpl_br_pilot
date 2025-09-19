package br.com.fplbr.pilot.common.infrastructure.web.resource;

import br.com.fplbr.pilot.common.application.service.CepService;
import br.com.fplbr.pilot.common.infrastructure.web.dto.CepResponseDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/cep")
@Tag(name = "Usuário", description = "APIs relacionadas ao usuário")
public class CepResource {
    
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
        return cepService.buscarCep(cep);
    }
}
