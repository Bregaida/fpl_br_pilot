package br.com.fplbr.pilot.aisweb.infrastructure.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.Map;

/**
 * Mapper para exceções da API AISWEB.
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException exception) {
        Map<String, Object> errorResponse = Map.of(
            "error", exception.getError(),
            "detail", exception.getDetail()
        );
        
        return Response.status(exception.getStatus())
                .entity(errorResponse)
                .build();
    }
}
