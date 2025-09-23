package br.com.fplbr.pilot.piloto.infrastructure.web.resource;

import br.com.fplbr.pilot.piloto.application.dto.PilotoDTO;
import br.com.fplbr.pilot.piloto.application.service.PilotoService;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Path("/api/v1/pilotos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Piloto", description = "Operações relacionadas ao cadastro de piloto")
@Slf4j
public class PilotoResource {

    @Inject
    PilotoService pilotoService;

    @POST
    @Operation(summary = "Cria um novo piloto", description = "Cadastra um novo piloto. Apenas um piloto pode ser cadastrado.")
    @APIResponse(responseCode = "201", description = "Piloto criado com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou piloto já cadastrado")
    public Response criarPiloto(@Valid PilotoDTO dto) {
        try {
            PilotoDTO pilotoCriado = pilotoService.criarPiloto(dto);
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(
                            "success", true,
                            "message", "Piloto cadastrado com sucesso",
                            "data", pilotoCriado
                    ))
                    .build();
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao criar piloto: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Erro interno ao criar piloto", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                            "success", false,
                            "message", "Erro interno do servidor"
                    ))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um piloto", description = "Atualiza os dados de um piloto existente")
    @APIResponse(responseCode = "200", description = "Piloto atualizado com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    @APIResponse(responseCode = "404", description = "Piloto não encontrado")
    public Response atualizarPiloto(@PathParam("id") Long id, @Valid PilotoDTO dto) {
        try {
            PilotoDTO pilotoAtualizado = pilotoService.atualizarPiloto(id, dto);
            return Response.ok(Map.of(
                    "success", true,
                    "message", "Piloto atualizado com sucesso",
                    "data", pilotoAtualizado
            )).build();
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao atualizar piloto ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Erro interno ao atualizar piloto ID {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                            "success", false,
                            "message", "Erro interno do servidor"
                    ))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca piloto por ID", description = "Retorna os dados de um piloto específico")
    @APIResponse(responseCode = "200", description = "Piloto encontrado")
    @APIResponse(responseCode = "404", description = "Piloto não encontrado")
    public Response buscarPilotoPorId(@PathParam("id") Long id) {
        return pilotoService.buscarPilotoPorId(id)
                .map(piloto -> Response.ok(Map.of(
                        "success", true,
                        "data", piloto
                )).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of(
                                "success", false,
                                "message", "Piloto não encontrado"
                        )).build());
    }

    @GET
    @Operation(summary = "Lista todos os pilotos", description = "Retorna lista de todos os pilotos cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de pilotos")
    public Response listarPilotos() {
        List<PilotoDTO> pilotos = pilotoService.listarPilotos();
        return Response.ok(Map.of(
                "success", true,
                "data", pilotos,
                "count", pilotos.size()
        )).build();
    }

    @GET
    @Path("/existe")
    @Operation(summary = "Verifica se existe piloto cadastrado", description = "Retorna se já existe um piloto cadastrado")
    @APIResponse(responseCode = "200", description = "Status da existência do piloto")
    public Response verificarExistenciaPiloto() {
        boolean existe = pilotoService.existePiloto();
        return Response.ok(Map.of(
                "success", true,
                "existe", existe
        )).build();
    }
}
