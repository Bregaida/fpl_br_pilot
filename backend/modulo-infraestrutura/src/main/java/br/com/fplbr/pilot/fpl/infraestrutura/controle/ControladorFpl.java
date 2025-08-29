package br.com.fplbr.pilot.fpl.infraestrutura.controle;

import br.com.fplbr.pilot.fpl.aplicacao.dto.RequisicaoPlanoDeVoo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.RespostaPlanoDeVoo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.jboss.logging.Logger;

@Path("/api/v1/fpl")
public class ControladorFpl {
    private static final Logger LOG = Logger.getLogger(ControladorFpl.class);

    @POST
    @Path("/preview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RespostaPlanoDeVoo preview(RequisicaoPlanoDeVoo req) {
        // Logs de entrada
        if (req == null) {
            LOG.warn("RequisicaoPlanoDeVoo nula recebida no /api/v1/fpl/preview");
        } else {
            LOG.infof("Preview FPL recebido: identificacao=%s, regras=%s, tipo=%s",
                    req.identificacaoAeronave, req.regrasDeVoo, req.tipoDeVoo);
        }

        // Caso de uso simples inline (evita CDI por enquanto)
        RespostaPlanoDeVoo resp = new RespostaPlanoDeVoo();
        String id = req != null && req.identificacaoAeronave != null ? req.identificacaoAeronave : "SEMID";
        String regras = req != null && req.regrasDeVoo != null ? req.regrasDeVoo : "IFR";
        String tipo = req != null && req.tipoDeVoo != null ? req.tipoDeVoo : "GERAL";
        resp.mensagemFpl = "(FPL-" + id + "-" + regras + "-" + tipo + ")";
        resp.avisos = List.of("Mensagem gerada em modo placeholder");
        LOG.debugf("Resposta gerada: %s", resp.mensagemFpl);
        return resp;
    }
}

