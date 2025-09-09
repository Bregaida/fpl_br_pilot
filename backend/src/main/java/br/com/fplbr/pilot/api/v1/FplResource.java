package br.com.fplbr.pilot.api.v1;

import br.com.fplbr.pilot.infrastructure.web.dto.PlanoDeVooDTO;
import jakarta.validation.Valid;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.fplbr.pilot.infrastructure.persistence.FplSubmission;
import jakarta.persistence.EntityManager;

@Path("/api/v1/fpl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FplResource {

    @Inject
    EntityManager em;

    @Inject
    ObjectMapper mapper;

    @POST
    @Transactional
    public Response criar(@Valid PlanoDeVooDTO dto) {
        // R1: horaPartida >= nowUTC + (15|45)
        int minutos = "PVC".equalsIgnoreCase(dto.getModo()) ? 45 : 15;
        OffsetDateTime min = OffsetDateTime.now().plusMinutes(minutos);
        if (dto.getHoraPartida() == null || dto.getHoraPartida().isBefore(min)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Erro("Hora de partida deve ser ≥ agora UTC + " + minutos + " min"))
                    .build();
        }

        // R2: tempoDeVooPrevisto <= autonomia
        int eet = hhmmToMinutes(dto.getTempoDeVooPrevisto());
        int aut = hhmmToMinutes(dto.getInformacaoSuplementar().getAutonomia());
        if (eet > aut) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Erro("EET não pode exceder a autonomia informada"))
                    .build();
        }

        // R3 (opcional/boa prática): rota contém REA ⇒ RMK deve conter REA
        if (dto.getRota() != null && dto.getRota().toUpperCase().contains("REA")) {
            String rmk = dto.getOutrasInformacoes() != null ? dto.getOutrasInformacoes().getRmk() : null;
            if (rmk == null || !rmk.toUpperCase().contains("REA")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new Erro("Quando a rota contiver REA, RMK/ deve conter REA e corredores"))
                        .build();
            }
        }

        // Regra adicional: se N = true, observações obrigatórias
        if (dto.getInformacaoSuplementar() != null && Boolean.TRUE.equals(dto.getInformacaoSuplementar().getN())) {
            String obs = dto.getInformacaoSuplementar().getObservacoes();
            if (obs == null || obs.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new Erro("Quando N = sim, Observações tornam-se obrigatórias"))
                        .build();
            }
        }

        try {
            String json = mapper.writeValueAsString(dto);
            FplSubmission entity = FplSubmission.builder()
                    .createdAt(OffsetDateTime.now())
                    .modo(dto.getModo())
                    .identificacao(dto.getIdentificacaoDaAeronave())
                    .payloadJson(json)
                    .build();
            em.persist(entity);
        } catch (Exception ignore) { }
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }

    private static int hhmmToMinutes(String hhmm) {
        if (hhmm == null || !hhmm.matches("^([0-1]\\d|2[0-3])[0-5]\\d$")) return Integer.MAX_VALUE;
        int h = Integer.parseInt(hhmm.substring(0, 2));
        int m = Integer.parseInt(hhmm.substring(2, 4));
        return h * 60 + m;
    }

    public static class Erro {
        public String message;
        public Erro(String message) { this.message = message; }
    }
}


