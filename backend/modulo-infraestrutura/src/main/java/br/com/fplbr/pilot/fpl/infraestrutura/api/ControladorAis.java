package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.ListarNotamsDoAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.Notam;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * NOTAM do aeródromo (stub).
 * Endpoint:
 *  - GET /api/v1/ais/{icao}/notams
 */
@Path("/api/v1/ais")
@Produces(MediaType.APPLICATION_JSON)
public class ControladorAis {

    @Inject
    PortaAisweb porta;

    @GET
    @Path("/{icao}/notams")
    public List<Notam> notams(@PathParam("icao") String icao) {
        ListarNotamsDoAerodromo caso = new ListarNotamsDoAerodromo(porta);
        return caso.executar(icao);
    }
}

