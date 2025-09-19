package br.com.fplbr.pilot.aisweb.application.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception thrown when a rate limit is exceeded.
 */
public class RateLimitExceededException extends RuntimeException {
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * @return The number of seconds after which the client can retry the request.
     */
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    /**
     * @return The HTTP status code to return when this exception is thrown.
     */
    public Response.Status getStatusCode() {
        return Response.Status.TOO_MANY_REQUESTS;
    }
}
