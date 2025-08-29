package br.com.fplbr.pilot.fpl.infraestrutura.api;

import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.GerarPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.RequisicaoPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.RespostaPlanoDeVoo;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/fpl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ControladorFpl {

    @Inject
    GerarPlanoDeVoo gerarPlano; // agora injetado

    @POST
    @Path("/preview")
    public RespostaPlanoDeVoo preview(RequisicaoPlanoDeVoo requisicao) {
        return gerarPlano.executar(requisicao);
    }
}

