package br.com.fplbr.pilot.flightplan.infrastructure.web;

import br.com.fplbr.pilot.flightplan.infrastructure.web.dto.FlightPlanDTO;
import br.com.fplbr.pilot.flightplan.application.service.FlightPlanService;
import br.com.fplbr.pilot.infrastructure.web.dto.PlanoDeVooDTO;
import jakarta.persistence.EntityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.List;

@ApplicationScoped
@Path("/api/v1/flightplans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Plano de Voo", description = "Operações relacionadas a planos de voo")
public class FlightPlanResource {

    private final FlightPlanService flightPlanService;

    @Inject
    ObjectMapper mapper;
    EntityManager em;

    public FlightPlanResource(FlightPlanService flightPlanService) {
        this.flightPlanService = flightPlanService;
    }

    @Inject
    public void setMapper(ObjectMapper mapper) { this.mapper = mapper; }

    @Inject
    public void setEntityManager(EntityManager em) { this.em = em; }

    @POST
    @Operation(summary = "Cria um novo plano de voo", description = "Recebe PlanoDeVooDTO e aplica as regras de validação")
    @APIResponse(responseCode = "201", description = "Plano de voo criado com sucesso", content = @Content(schema = @Schema(implementation = PlanoDeVooDTO.class)))
    @APIResponse(responseCode = "400", description = "Entrada inválida")
    @Transactional
    public Response createFlightPlan(@Valid PlanoDeVooDTO dto) {
        int minutos = "PVC".equalsIgnoreCase(dto.getModo()) ? 45 : 15;
        java.time.OffsetDateTime min = java.time.OffsetDateTime.now().plusMinutes(minutos);
        if (dto.getHoraPartida() == null || dto.getHoraPartida().isBefore(min)) {
            return badRequest("horaPartida", "Hora de partida deve ser ≥ agora UTC + " + minutos + " min");
        }
        int eet = hhmmToMinutes(dto.getTempoDeVooPrevisto());
        int aut = hhmmToMinutes(dto.getInformacaoSuplementar().getAutonomia());
        if (eet > aut) {
            return badRequest("tempoDeVooPrevisto", "EET não pode exceder a autonomia informada");
        }
        String rmk = dto.getOutrasInformacoes() != null ? dto.getOutrasInformacoes().getRmk() : null;
        if (rmk == null || rmk.isBlank()) {
            return badRequest("outrasInformacoes.rmk", "RMK/ é obrigatório");
        }
        if (dto.getRota() != null && dto.getRota().toUpperCase().contains("REA") && !rmk.toUpperCase().contains("REA")) {
            return badRequest("outrasInformacoes.rmk", "Quando a rota contiver REA, RMK/ deve conter REA e corredores");
        }
        if (dto.getInformacaoSuplementar() != null && Boolean.TRUE.equals(dto.getInformacaoSuplementar().getN())) {
            String obs = dto.getInformacaoSuplementar().getObservacoes();
            if (obs == null || obs.isBlank()) {
                return badRequest("informacaoSuplementar.observacoes", "Quando N = sim, Observações tornam-se obrigatórias");
            }
        }
        br.com.fplbr.pilot.infrastructure.persistence.FplSubmission entity = null;
        try {
            String json = mapper.writeValueAsString(dto);
            entity = br.com.fplbr.pilot.infrastructure.persistence.FplSubmission.builder()
                    .createdAt(java.time.OffsetDateTime.now())
                    .modo(dto.getModo())
                    .identificacao(dto.getIdentificacaoDaAeronave())
                    .payloadJson(json)
                    .build();
            em.persist(entity);
        } catch (Exception ignore) {}
        URI location = entity != null && entity.getId() != null
                ? URI.create("/api/v1/flightplans/submissions/" + entity.getId())
                : URI.create("/api/v1/flightplans/submissions");
        var body = java.util.Map.of(
                "id", entity != null ? entity.getId() : null,
                "createdAt", entity != null ? entity.getCreatedAt() : null,
                "modo", dto.getModo(),
                "identificacao", dto.getIdentificacaoDaAeronave()
        );
        return Response.created(location).entity(body).build();
    }

    private static int hhmmToMinutes(String hhmm) {
        if (hhmm == null || !hhmm.matches("^([0-1]\\d|2[0-3])[0-5]\\d$")) return Integer.MAX_VALUE;
        int h = Integer.parseInt(hhmm.substring(0, 2));
        int m = Integer.parseInt(hhmm.substring(2, 4));
        return h * 60 + m;
    }

    private static Response badRequest(String field, String message) {
        java.util.Map<String, Object> body = java.util.Map.of(
                "message", "Entrada inválida",
                "errors", java.util.List.of(java.util.Map.of("field", field, "message", message))
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    @GET
    @Path("/submissions")
    @Operation(summary = "Lista envios recebidos", description = "Lista FPLs recebidos (tabela FplSubmission)")
    public Response listSubmissions(@QueryParam("ident") String ident, @QueryParam("limit") @DefaultValue("10") int limit) {
        String jpql = "select f from FplSubmission f" + (ident != null && !ident.isBlank() ? " where f.identificacao = :ident" : "") + " order by f.createdAt desc";
        var q = em.createQuery(jpql, br.com.fplbr.pilot.infrastructure.persistence.FplSubmission.class);
        if (ident != null && !ident.isBlank()) q.setParameter("ident", ident);
        q.setMaxResults(Math.max(1, Math.min(limit, 100)));
        var list = q.getResultList();
        var out = list.stream().map(f -> {
            String dep = null, dest = null, tipo = null, regra = null;
            try {
                var tree = mapper.readTree(f.getPayloadJson());
                dep = tree.path("aerodromoDePartida").asText(null);
                dest = tree.path("aerodromoDeDestino").asText(null);
                tipo = tree.path("tipoDeVooEnum").asText(null);
                regra = tree.path("regraDeVooEnum").asText(null);
            } catch (Exception ignore) {}
            return java.util.Map.of(
                    "id", f.getId(),
                    "createdAt", f.getCreatedAt(),
                    "modo", f.getModo(),
                    "identificacao", f.getIdentificacao(),
                    "aerodromoDePartida", dep,
                    "aerodromoDeDestino", dest,
                    "tipoDeVooEnum", tipo,
                    "regraDeVooEnum", regra
            );
        }).toList();
        return Response.ok(out).build();
    }

    @GET
    @Path("/submissions/{id}")
    @Operation(summary = "Obtém um envio por ID", description = "Retorna o envio com payload original")
    public Response getSubmissionById(@PathParam("id") Long id) {
        var f = em.find(br.com.fplbr.pilot.infrastructure.persistence.FplSubmission.class, id);
        if (f == null) return Response.status(Response.Status.NOT_FOUND).build();
        Object payload;
        try {
            payload = mapper.readValue(f.getPayloadJson(), java.util.Map.class);
        } catch (Exception e) {
            payload = f.getPayloadJson();
        }
        var out = java.util.Map.of(
                "id", f.getId(),
                "createdAt", f.getCreatedAt(),
                "modo", f.getModo(),
                "identificacao", f.getIdentificacao(),
                "payload", payload
        );
        return Response.ok(out).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Obtém um plano de voo por ID",
        description = "Returns the flight plan with the specified ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Plano de voo encontrado",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO.class))
    )
    @APIResponse(
        responseCode = "404",
        description = "Plano de voo não encontrado"
    )
    public Response getFlightPlanById(
        @Parameter(description = "ID of the flight plan", required = true)
        @PathParam("id") Long id
    ) {
        FlightPlanDTO flightPlan = flightPlanService.getFlightPlanById(id);
        return Response.ok(flightPlan).build();
    }

    @GET
    @Operation(
        summary = "Obtém todos os planos de voo",
        description = "Retorna uma lista de todos os planos de voo"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de todos os planos de voo",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO[].class))
    )
    public Response getAllFlightPlans() {
        List<FlightPlanDTO> flightPlans = flightPlanService.getAllFlightPlans();
        return Response.ok(flightPlans).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Atualiza um plano de voo",
        description = "Atualiza um plano de voo existente com os detalhes fornecidos"
    )
    @APIResponse(
        responseCode = "200",
        description = "Plano de voo atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Entrada inválida"
    )
    @APIResponse(
        responseCode = "404",
        description = "Plano de voo não encontrado"
    )
    public Response updateFlightPlan(
        @Parameter(description = "ID of the flight plan", required = true)
        @PathParam("id") Long id,
        @Valid FlightPlanDTO flightPlanDTO
    ) {
        FlightPlanDTO updatedFlightPlan = flightPlanService.updateFlightPlan(id, flightPlanDTO);
        return Response.ok(updatedFlightPlan).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Deleta um plano de voo",
        description = "Deleta o plano de voo com o ID especificado"
    )
    @APIResponse(
        responseCode = "204",
        description = "Plano de voo deletado com sucesso"
    )
    @APIResponse(
        responseCode = "404",
        description = "Plano de voo não encontrado"
    )
    public Response deleteFlightPlan(
        @Parameter(description = "ID do plano de voo", required = true)
        @PathParam("id") Long id
    ) {
        flightPlanService.deleteFlightPlan(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/pilot/{pilotName}")
    @Operation(
        summary = "Obtém planos de voo por nome do piloto",
        description = "Retorna uma lista de planos de voo para o piloto especificado"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de planos de voo para o piloto",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO[].class))
    )
    public Response getFlightPlansByPilot(
        @Parameter(description = "Nome do piloto", required = true)
        @PathParam("pilotName") String pilotName
    ) {
        List<FlightPlanDTO> flightPlans = flightPlanService.getFlightPlansByPilot(pilotName);
        return Response.ok(flightPlans).build();
    }

    @GET
    @Path("/aircraft/{aircraftRegistration}")
    @Operation(
        summary = "Obtém planos de voo por registro da aeronave",
        description = "Retorna uma lista de planos de voo para a aeronave especificada"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de planos de voo para a aeronave",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO[].class))
    )
    public Response getFlightPlansByAircraft(
        @Parameter(description = "Registration of the aircraft", required = true)
        @PathParam("aircraftRegistration") String aircraftRegistration
    ) {
        List<FlightPlanDTO> flightPlans = flightPlanService.getFlightPlansByAircraft(aircraftRegistration);
        return Response.ok(flightPlans).build();
    }
}
