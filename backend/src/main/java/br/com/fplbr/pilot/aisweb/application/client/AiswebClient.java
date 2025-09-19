package br.com.fplbr.pilot.aisweb.application.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Interface para o cliente REST da API AISWEB.
 */
@RegisterRestClient
public interface AiswebClient {

    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getRotaerData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaoCode") String icaoCode
    );
    
    /**
     * Busca dados de cartas aeronáuticas para um aeródromo específico.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getCartasData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaoCode") String icaoCode
    );
    
    /**
     * Busca dados do sol (nascer e pôr do sol) para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getSunData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaoCode") String icaoCode
    );
    
    /**
     * Busca METAR e TAF (condições meteorológicas) para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getMetarData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaoCode") String icaoCode
    );
    
    /**
     * Busca informações temporárias para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getInfotempData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaoCode") String icaoCode,
        @QueryParam("status") String status
    );

    /**
     * Busca dados AIP para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getPubAipData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("type") String type
    );

    /**
     * Busca dados AIXM para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getPubAixmData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("type") String type
    );

    /**
     * Busca suplementos para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getSuplementosData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("IcaoCode") String icaoCode
    );

    /**
     * Busca NOTAM para um aeródromo.
     */
    @GET
    @Path("/api/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getNotamData(
        @QueryParam("apiKey") String apiKey,
        @QueryParam("apiPass") String apiPass,
        @QueryParam("area") String area,
        @QueryParam("icaocode") String icaoCode
    );
}
