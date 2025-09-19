package br.com.fplbr.pilot.aisweb.application.annotation;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to mark methods that should be rate limited.
 * The rate limiting is applied per method and per user (if authenticated).
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface RateLimited {
    /**
     * @return The maximum number of requests allowed within the time window.
     */
    int limit() default 100;

    /**
     * @return The time window in which the limit applies.
     */
    int duration() default 1;

    /**
     * @return The time unit for the duration.
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
