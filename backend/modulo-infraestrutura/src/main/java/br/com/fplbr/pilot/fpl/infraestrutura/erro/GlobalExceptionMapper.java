package br.com.fplbr.pilot.fpl.infraestrutura.erro;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        if (exception instanceof BadRequestException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        }
        LOG.errorf(exception, "Erro ao processar requisição: %s", exception.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("erro", exception.getClass().getSimpleName());
        body.put("mensagem", exception.getMessage());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
