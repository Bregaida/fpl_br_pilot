package br.com.fplbr.pilot.infrastructure.resource.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<Map<String, String>> errors = exception.getConstraintViolations()
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());

        Map<String, Object> body = Map.of(
                "message", "Entrada inválida",
                "errors", errors
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private Map<String, String> toEntry(ConstraintViolation<?> v) {
        String field = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        String msg = v.getMessage();
        return Map.of("field", field, "message", msg);
    }
}


