package br.com.fplbr.pilot.common.infrastructure.web;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(CorsFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOG.info("CORS Filter applied to: " + requestContext.getUriInfo().getPath());
        LOG.info("Origin: " + requestContext.getHeaderString("Origin"));
        LOG.info("Method: " + requestContext.getMethod());
        
        // Get the origin from the request
        String origin = requestContext.getHeaderString("Origin");
        
        // Allow both development ports and any localhost port
        if (origin != null && (origin.startsWith("http://localhost:") || origin.startsWith("https://localhost:"))) {
            LOG.info("Allowing origin: " + origin);
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        } else {
            // Fallback to common development ports
            LOG.info("Using fallback origin for: " + origin);
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5180");
        }
        
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, cache-control");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
        
        // Handle preflight requests
        if (requestContext.getMethod().equals("OPTIONS")) {
            LOG.info("Handling OPTIONS preflight request");
            responseContext.setStatus(200);
        }
    }
}
