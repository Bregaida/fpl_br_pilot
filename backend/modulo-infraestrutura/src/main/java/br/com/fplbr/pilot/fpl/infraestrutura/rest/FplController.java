package br.com.fplbr.pilot.fpl.infraestrutura.rest;

import br.com.fplbr.pilot.fpl.aplicacao.dto.FplDTO;
import br.com.fplbr.pilot.fpl.aplicacao.servicos.FplMessageBuilder;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/fpl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FplController {

  @POST
  @Path("/preview")
  public Response preview(@Valid FplDTO dto) {
    String ats = FplMessageBuilder.toAts(dto);
    Map<String,Object> out = new HashMap<>();
    out.put("ats", ats);
    out.put("normalized", dto);
    return Response.ok(out).build();
  }
}
