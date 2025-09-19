package br.com.fplbr.pilot.aisweb.application.interceptor;

import br.com.fplbr.pilot.aisweb.application.annotation.RateLimited;
import br.com.fplbr.pilot.aisweb.application.exception.RateLimitExceededException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Interceptor that enforces rate limiting on methods annotated with @RateLimited.
 */
@Interceptor
@RateLimited
@Priority(Interceptor.Priority.APPLICATION)
public class RateLimitInterceptor {
    private static final Logger LOG = Logger.getLogger(RateLimitInterceptor.class);

    @Context
    SecurityContext securityContext;

    @Inject
    @ConfigProperty(name = "quarkus.http.limits.requests")
    int globalRateLimit;

    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    @AroundInvoke
    public Object rateLimit(InvocationContext context) throws Exception {
        RateLimited rateLimited = getRateLimitedAnnotation(context);
        if (rateLimited == null) {
            return context.proceed();
        }

        String clientId = getClientIdentifier();
        String methodKey = getMethodKey(context, clientId);
        long now = System.currentTimeMillis();

        // Get or create the request counter for this method and client
        RequestCounter counter = requestCounters.computeIfAbsent(methodKey, 
            k -> new RequestCounter(rateLimited.limit(), rateLimited.duration(), rateLimited.timeUnit()));

        // Check if the rate limit is exceeded
        synchronized (counter) {
            if (counter.isRateLimited(now)) {
                long retryAfter = counter.getRetryAfter(now);
                LOG.warnf("Rate limit exceeded for %s. Retry after %d seconds", 
                         methodKey, TimeUnit.MILLISECONDS.toSeconds(retryAfter));
                
                throw new RateLimitExceededException(
                    String.format("Rate limit exceeded. Try again in %d seconds", 
                                TimeUnit.MILLISECONDS.toSeconds(retryAfter)),
                    TimeUnit.MILLISECONDS.toSeconds(retryAfter));
            }
            
            // Increment the request count
            counter.increment(now);
        }

        try {
            return context.proceed();
        } catch (Exception e) {
            throw e;
        }
    }

    private RateLimited getRateLimitedAnnotation(InvocationContext context) {
        // Check method first, then class
        RateLimited rateLimited = context.getMethod().getAnnotation(RateLimited.class);
        if (rateLimited == null) {
            rateLimited = context.getTarget().getClass().getAnnotation(RateLimited.class);
        }
        return rateLimited;
    }

    private String getClientIdentifier() {
        // Use the authenticated user's name if available, otherwise use the IP address
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        // In a real application, you might want to get the client IP here
        return "anonymous";
    }

    private String getMethodKey(InvocationContext context, String clientId) {
        return String.format("%s#%s:%s", 
            context.getTarget().getClass().getSimpleName(),
            context.getMethod().getName(),
            clientId);
    }

    /**
     * Inner class to track request counts within a time window.
     */
    private static class RequestCounter {
        private final int limit;
        private final long windowMillis;
        private int count;
        private long windowStart;

        public RequestCounter(int limit, int duration, TimeUnit timeUnit) {
            this.limit = limit;
            this.windowMillis = timeUnit.toMillis(duration);
            this.count = 0;
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized boolean isRateLimited(long now) {
            updateWindow(now);
            return count >= limit;
        }

        public synchronized void increment(long now) {
            updateWindow(now);
            count++;
        }

        public synchronized long getRetryAfter(long now) {
            long timePassed = now - windowStart;
            return windowMillis - timePassed;
        }

        private void updateWindow(long now) {
            long timePassed = now - windowStart;
            if (timePassed > windowMillis) {
                // Reset the counter for a new time window
                count = 0;
                windowStart = now;
            }
        }
    }
}
