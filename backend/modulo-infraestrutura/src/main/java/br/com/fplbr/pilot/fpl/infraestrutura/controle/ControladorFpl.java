package br.com.fplbr.pilot.fpl.infraestrutura.controle;

import br.com.fplbr.pilot.fpl.aplicacao.dto.RequisicaoPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.RespostaPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.casosdeuso.GerarPlanoDeVoo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Deprecated
@Path("/_legacy/fpl")
public class ControladorFpl {
    private static final Logger LOG = Logger.getLogger(ControladorFpl.class);

    @POST
    @Path("/preview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RespostaPlanoDeVoo preview(RequisicaoPlanoDeVoo req) {
        // Logs de entrada
        if (req == null) {
            LOG.warn("RequisicaoPlanoDeVoo nula recebida no /_legacy/fpl/preview");
        } else {
            LOG.infof("Preview FPL recebido: identificacao=%s, regras=%s, tipo=%s",
                    req.identificacaoAeronave, req.regrasDeVoo, req.tipoDeVoo);
        }

        // Usa caso de uso formal
        GerarPlanoDeVoo caso = new GerarPlanoDeVoo();
        RespostaPlanoDeVoo resp = caso.executar(req);
        LOG.debugf("Resposta gerada: %s", resp.mensagemFpl);
        return resp;
    }
}


