package br.com.aisweb.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

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
