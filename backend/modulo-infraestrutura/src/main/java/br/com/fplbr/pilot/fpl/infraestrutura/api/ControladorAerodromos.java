package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.ObterDetalhesDoAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.ListarCartasDoAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.CartaAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/v1/aerodromos")
@Produces(MediaType.APPLICATION_JSON)
public class ControladorAerodromos {

    @Inject
    PortaAisweb porta;

    @GET
    @Path("/{icao}")
    public Aerodromo detalhes(@PathParam("icao") String icao) {
        ObterDetalhesDoAerodromo casoDetalhes = new ObterDetalhesDoAerodromo();
        return casoDetalhes.executar(icao);
    }

    @GET
    @Path("/{icao}/cartas")
    public List<CartaAerodromo> cartas(@PathParam("icao") String icao) {
        ListarCartasDoAerodromo casoCartas = new ListarCartasDoAerodromo(porta);
        return casoCartas.executar(icao);
    }
}

