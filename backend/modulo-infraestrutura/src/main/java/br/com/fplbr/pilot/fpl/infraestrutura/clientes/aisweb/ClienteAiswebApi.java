package br.com.fplbr.pilot.fpl.infraestrutura.clientes.aisweb;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "aisweb-api")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ClienteAiswebApi {

    // Exemplos de rotas; ajuste conforme o provedor
    @GET
    @Path("/notam/{icao}")
    @ClientQueryParam(name = "token", value = "${aisweb.token}")
    String listarNotams(@PathParam("icao") String icao);

    @GET
    @Path("/aerodromos/{icao}/cartas")
    @ClientQueryParam(name = "token", value = "${aisweb.token}")
    String listarCartas(@PathParam("icao") String icao);

    @GET
    @Path("/aerodromos/{icao}")
    @ClientQueryParam(name = "token", value = "${aisweb.token}")
    String obterAerodromo(@PathParam("icao") String icao);
}
