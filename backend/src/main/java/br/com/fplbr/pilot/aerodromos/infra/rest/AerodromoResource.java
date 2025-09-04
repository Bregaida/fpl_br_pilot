package br.com.fplbr.pilot.aerodromos.infra.rest;

import br.com.fplbr.pilot.aerodromos.application.service.AerodromoService;
import br.com.fplbr.pilot.aerodromos.dto.AerodromoDTO;
import br.com.fplbr.pilot.aerodromos.dto.CartaAerodromoDTO;
import br.com.fplbr.pilot.aerodromos.ports.in.SolServicePort;
import br.com.fplbr.pilot.aerodromos.ports.out.CartaRepositoryPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Path("/api/aerodromos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Aeródromos", description = "Operações relacionadas a aeródromos")
public class AerodromoResource {

    @Inject
    AerodromoService aerodromoService;
    
    @Inject
    CartaRepositoryPort cartaRepository;
    
    @Inject
    SolServicePort solService;

    @GET
    @Path("/{icao}")
    @Operation(summary = "Busca um aeródromo por código ICAO")
    @APIResponse(responseCode = "200", description = "Aeródromo encontrado", content = @Content(schema = @Schema(implementation = AerodromoDTO.class)))
    @APIResponse(responseCode = "404", description = "Aeródromo não encontrado")
    public Response buscarPorIcao(
            @PathParam("icao") String icao,
            @QueryParam("dof") String dataVoo) {
        
        // Get aerodrome information
        AerodromoDTO aerodromo = aerodromoService.buscarPorIcao(icao)
                .orElseThrow(() -> new WebApplicationException("Aeródromo não encontrado", Response.Status.NOT_FOUND));
        
        // Get charts for the aerodrome
        List<CartaAerodromoDTO> cartas = cartaRepository.porIcao(icao);
        aerodromo.setCartas(cartas);
        
        // Calculate sunrise/sunset for the specified date or today
        LocalDate data = dataVoo != null ? LocalDate.parse(dataVoo) : LocalDate.now();
        var solInfo = solService.obterSol(icao, data);
        aerodromo.setNascerSol(solInfo.sunrise());
        aerodromo.setPorDoSol(solInfo.sunset());
        
        return Response.ok(aerodromo).build();
    }
    
    @GET
    @Path("/{icao}/cartas")
    @Operation(summary = "Lista todas as cartas disponíveis para um aeródromo")
    @APIResponse(responseCode = "200", description = "Lista de cartas encontradas", 
                content = @Content(schema = @Schema(implementation = CartaAerodromoDTO.class, type = "array")))
    public Response listarCartas(@PathParam("icao") String icao) {
        List<CartaAerodromoDTO> cartas = cartaRepository.porIcao(icao);
        return Response.ok(cartas).build();
    }
    
    @GET
    @Path("/{icao}/sol")
    @Operation(summary = "Obtém informações de nascer e pôr do sol para um aeródromo")
    @APIResponse(responseCode = "200", description = "Informações de sol encontradas")
    public Response obterSolInfo(
            @PathParam("icao") String icao,
            @QueryParam("data") String dataStr) {
        
        LocalDate data = dataStr != null ? LocalDate.parse(dataStr) : LocalDate.now();
        var solInfo = solService.obterSol(icao, data);
        
        return Response.ok(solInfo).build();
    }
}
