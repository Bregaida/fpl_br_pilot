package br.com.fplbr.pilot.fpl.infraestrutura.clientes.redemet;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "redemet-api")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ClienteRedemetApi {

    // Ajuste os paths de acordo com o provedor. Mantive como exemplo:
    @GET
    @Path("/metar/{icao}")
    @ClientQueryParam(name = "token", value = "${redemet.token}")
    String obterMetar(@PathParam("icao") String icao);

    @GET
    @Path("/taf/{icao}")
    @ClientQueryParam(name = "token", value = "${redemet.token}")
    String obterTaf(@PathParam("icao") String icao);

    // Opcional: SIGMET, se disponível
    @GET
    @Path("/sigmet/{icao}")
    @ClientQueryParam(name = "token", value = "${redemet.token}")
    String obterSigmet(@PathParam("icao") String icao);
}

