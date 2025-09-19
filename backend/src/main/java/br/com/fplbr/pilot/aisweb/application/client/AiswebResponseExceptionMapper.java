package br.com.fplbr.pilot.aisweb.application.client;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class AiswebResponseExceptionMapper implements ResponseExceptionMapper<RuntimeException> {
    private static final Logger LOG = Logger.getLogger(AiswebResponseExceptionMapper.class);

    @Override
    public RuntimeException toThrowable(Response response) {
        if (response.getStatus() >= 400) {
            String errorMsg = String.format("AISWEB API Error - Status: %d, Reason: %s",
                    response.getStatus(), response.getStatusInfo().getReasonPhrase());
            LOG.error(errorMsg);
            return new RuntimeException(errorMsg);
        }
        return null;
    }
}
