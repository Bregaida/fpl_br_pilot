package br.com.fplbr.pilot.flightplan.infrastructure.web;

import br.com.fplbr.pilot.flightplan.infrastructure.web.dto.FlightPlanDTO;
import br.com.fplbr.pilot.flightplan.application.service.FlightPlanService;
import jakarta.validation.Valid;
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
@Tag(name = "Flight Plans", description = "Operations related to flight plans")
public class FlightPlanResource {

    private final FlightPlanService flightPlanService;

    @Inject
    public FlightPlanResource(FlightPlanService flightPlanService) {
        this.flightPlanService = flightPlanService;
    }

    @POST
    @Operation(
        summary = "Create a new flight plan",
        description = "Creates a new flight plan with the provided details"
    )
    @APIResponse(
        responseCode = "201",
        description = "Flight plan created successfully",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input"
    )
    public Response createFlightPlan(@Valid FlightPlanDTO flightPlanDTO) {
        FlightPlanDTO createdFlightPlan = flightPlanService.createFlightPlan(flightPlanDTO);
        URI location = (createdFlightPlan.getId() != null)
            ? URI.create("/api/v1/flightplans/" + createdFlightPlan.getId())
            : null;
        if (location != null) {
            return Response.created(location).entity(createdFlightPlan).build();
        }
        return Response.status(Response.Status.CREATED).entity(createdFlightPlan).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get flight plan by ID",
        description = "Returns the flight plan with the specified ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Flight plan found",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO.class))
    )
    @APIResponse(
        responseCode = "404",
        description = "Flight plan not found"
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
        summary = "Get all flight plans",
        description = "Returns a list of all flight plans"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of all flight plans",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO[].class))
    )
    public Response getAllFlightPlans() {
        List<FlightPlanDTO> flightPlans = flightPlanService.getAllFlightPlans();
        return Response.ok(flightPlans).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Update a flight plan",
        description = "Updates an existing flight plan with the provided details"
    )
    @APIResponse(
        responseCode = "200",
        description = "Flight plan updated successfully",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input"
    )
    @APIResponse(
        responseCode = "404",
        description = "Flight plan not found"
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
        summary = "Delete a flight plan",
        description = "Deletes the flight plan with the specified ID"
    )
    @APIResponse(
        responseCode = "204",
        description = "Flight plan deleted successfully"
    )
    @APIResponse(
        responseCode = "404",
        description = "Flight plan not found"
    )
    public Response deleteFlightPlan(
        @Parameter(description = "ID of the flight plan", required = true)
        @PathParam("id") Long id
    ) {
        flightPlanService.deleteFlightPlan(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/pilot/{pilotName}")
    @Operation(
        summary = "Get flight plans by pilot name",
        description = "Returns a list of flight plans for the specified pilot"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of flight plans for the pilot",
        content = @Content(schema = @Schema(implementation = FlightPlanDTO[].class))
    )
    public Response getFlightPlansByPilot(
        @Parameter(description = "Name of the pilot", required = true)
        @PathParam("pilotName") String pilotName
    ) {
        List<FlightPlanDTO> flightPlans = flightPlanService.getFlightPlansByPilot(pilotName);
        return Response.ok(flightPlans).build();
    }

    @GET
    @Path("/aircraft/{aircraftRegistration}")
    @Operation(
        summary = "Get flight plans by aircraft registration",
        description = "Returns a list of flight plans for the specified aircraft"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of flight plans for the aircraft",
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
