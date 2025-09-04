package br.com.fplbr.pilot.aerodromos.infra.http;

import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.core5.util.Timeout;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@ApplicationScoped
public class HttpCartasZipDownloader implements CartasZipDownloaderPort {

    private static final Logger LOG = Logger.getLogger(HttpCartasZipDownloader.class);
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int RESPONSE_TIMEOUT_MS = 30_000;

    @ConfigProperty(name = "aisweb.cartas.zip.url")
    String zipUrl;

    @Override
    public byte[] baixarZip() {
        Objects.requireNonNull(zipUrl, "URL do ZIP não pode ser nula");

        try (Executor executor = Executor.newInstance()) {
            return executor.execute(Request.get(zipUrl)
                    .connectTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT_MS))
                    .responseTimeout(Timeout.ofMilliseconds(RESPONSE_TIMEOUT_MS)))
                .returnContent()
                .asBytes();
        } catch (IOException e) {
            String errorMsg = String.format("Falha ao baixar ZIP de cartas de %s: %s",
                    zipUrl, e.getMessage());
            LOG.error(errorMsg, e);
            throw new IllegalStateException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Erro inesperado ao baixar ZIP de %s: %s",
                    zipUrl, e.getMessage());
            LOG.error(errorMsg, e);
            throw new IllegalStateException(errorMsg, e);
        }
    }
}
