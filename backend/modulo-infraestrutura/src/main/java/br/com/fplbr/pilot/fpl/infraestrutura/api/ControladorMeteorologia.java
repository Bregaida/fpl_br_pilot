package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.ObterBriefingMeteorologico;
import br.com.fplbr.pilot.fpl.aplicacao.dto.BriefingMeteorologico;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaMeteorologia;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Briefing meteorológico consolidado (stub).
 * Endpoints:
 *  - GET /api/v1/meteorologia/{icao}/briefing
 */
@Path("/api/v1/meteorologia")
@Produces(MediaType.APPLICATION_JSON)
public class ControladorMeteorologia {

    @Inject
    PortaMeteorologia porta;

    @GET
    @Path("/{icao}/briefing")
    public BriefingMeteorologico briefing(@PathParam("icao") String icao) {
        ObterBriefingMeteorologico caso = new ObterBriefingMeteorologico(porta);
        return caso.executar(icao);
    }
}

