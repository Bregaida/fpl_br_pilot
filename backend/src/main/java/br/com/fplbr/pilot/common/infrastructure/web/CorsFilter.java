package br.com.fplbr.pilot.common.infrastructure.web;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(CorsFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOG.info("CORS Filter applied to: " + requestContext.getUriInfo().getPath());
        
        // Get the origin from the request
        String origin = requestContext.getHeaderString("Origin");
        
        // Allow both development ports
        if (origin != null && (origin.equals("http://localhost:5173") || origin.equals("http://localhost:5180"))) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        } else {
            // Fallback to 5173 if no valid origin
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        }
        
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
        
        // Handle preflight requests
        if (requestContext.getMethod().equals("OPTIONS")) {
            responseContext.setStatus(200);
        }
    }
}
