package br.com.fplbr.pilot.aisweb.infrastructure.resource;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.application.service.*;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * Resource para endpoints AISWEB.
 */
@Path("/api/aisweb")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiswebResource {
    private static final Logger LOG = Logger.getLogger(AiswebResource.class);

    @Inject
    RotaerService rotaerService;

    @Inject
    MeteoService meteoService;

    @Inject
    CartasService cartasService;

    @Inject
    InfotempService infotempService;

    @Inject
    PubAipService pubAipService;

    @Inject
    PubAixmService pubAixmService;

    @Inject
    SuplementosService suplementosService;

    @Inject
    NotamService notamService;

    @Inject
    AiswebFullService aiswebFullService;

    @Inject
    SunService sunService;

    @GET
    @Path("/test-logs")
    public Response testLogs() {
        LOG.info("🧪 [TEST-LOGS] Testando logs - INFO level");
        LOG.warn("🧪 [TEST-LOGS] Testando logs - WARN level");
        LOG.error("🧪 [TEST-LOGS] Testando logs - ERROR level");
        return Response.ok("Logs testados - verifique o console da aplicação").build();
    }


    /**
     * Endpoint para buscar dados ROTAER de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return Dados ROTAER do aeródromo
     */
    @GET
    @Path("/rotaer/{icao}")
    public Response buscarRotaer(@PathParam("icao") String icao) {
        try {
            if (icao == null || icao.trim().length() != 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Código ICAO inválido. Deve conter 4 caracteres.")
                    .build();
            }

            RotaerDto rotaer = rotaerService.buscar(icao.toUpperCase());
            if (rotaer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Dados ROTAER não encontrados para o aeródromo: " + icao)
                    .build();
            }

            return Response.ok(rotaer).build();

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro ao processar requisição ROTAER para %s: %s",
                icao, e.getMessage());
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar dados INFOTEMP de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados INFOTEMP do aeródromo
     */
    /**
     * Endpoint para buscar informações de temperatura de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo (4 caracteres)
     * @return Informações de temperatura do aeródromo
     */
    @GET
    @Path("/infotemp/{icao}")
    public Response buscarInfotemp(@PathParam("icao") String icao) {
        try {
            if (icao == null || icao.trim().length() != 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Código ICAO inválido. Deve conter 4 caracteres.")
                    .build();
            }

            InfotempDto infotemp = infotempService.buscar(icao.toUpperCase());
            if (infotemp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Dados de temperatura não encontrados para o aeródromo: " + icao)
                    .build();
            }

            return Response.ok(infotemp).build();

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro ao processar requisição INFOTEMP para %s: %s",
                icao, e.getMessage());
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar dados meteorológicos de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados meteorológicos do aeródromo
     */
    @GET
    @Path("/meteo/{icao}")
    public MeteoDto buscarMeteo(@PathParam("icao") String icao) {
        return meteoService.buscar(icao);
    }

    /**
     * Endpoint para buscar dados de cartas de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados de cartas do aeródromo
     */
    @GET
    @Path("/cartas/{icao}")
    public CartasDto buscarCartas(@PathParam("icao") String icao) {
        return cartasService.buscar(icao);
    }

    /**
     * Endpoint para listar dados AIP.
     *
     * @return Dados AIP
     */
    @GET
    @Path("/pub/aip")
    public PubAipDto listarAip() {
        return pubAipService.listar();
    }

    /**
     * Endpoint para listar dados AIXM.
     *
     * @return Dados AIXM
     */
    @GET
    @Path("/pub/aixm")
    public PubAixmDto listarAixm() {
        return pubAixmService.listar();
    }

    /**
     * Endpoint para buscar dados de suplementos de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados de suplementos do aeródromo
     */
    @GET
    @Path("/suplementos/{icao}")
    public SuplementosDto buscarSuplementos(@PathParam("icao") String icao) {
        return suplementosService.buscar(icao);
    }

    /**
     * Endpoint para buscar dados NOTAM de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados NOTAM do aeródromo
     */
    @GET
    @Path("/notam/{icao}")
    public NotamDto buscarNotam(@PathParam("icao") String icao) {
        return notamService.buscar(icao);
    }

    /**
     * Endpoint para buscar dados de sol de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados de sol do aeródromo
     */
    @GET
    @Path("/sun/{icao}")
    public Response buscarSun(@PathParam("icao") String icao) {
        try {
            if (icao == null || icao.trim().length() != 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Código ICAO inválido. Deve conter 4 caracteres.")
                    .build();
            }

            SunDto sun = sunService.buscar(icao.toUpperCase());
            if (sun == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Dados de sol não encontrados para o aeródromo: " + icao)
                    .build();
            }

            return Response.ok(sun).build();

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Erro ao processar requisição SUN para %s: %s",
                icao, e.getMessage());
            throw new WebApplicationException(errorMsg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar dados completos de um aeródromo.
     *
     * @param icao Código ICAO do aeródromo
     * @return Dados completos do aeródromo
     */
    @GET
    @Path("/full/{icao}")
    public Response buscarCompleto(@PathParam("icao") String icao) {
        try {
            if (icao == null || icao.trim().length() != 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Código ICAO inválido. Deve conter 4 caracteres.")
                    .build();
            }

            Map<String, Object> dados = aiswebFullService.buscarDadosCompletos(icao.toUpperCase());
            return Response.ok(dados).build();

        } catch (Exception e) {
            String errorMsg = String.format("Erro ao processar requisição completa para %s: %s",
                icao, e.getMessage());
            LOG.error(errorMsg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMsg)
                .build();
        }
    }
}
