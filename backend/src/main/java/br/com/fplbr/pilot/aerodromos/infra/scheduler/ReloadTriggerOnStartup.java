package br.com.fplbr.pilot.aerodromos.infra.scheduler;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@ApplicationScoped
public class ReloadTriggerOnStartup {

    void onStart(@Observes StartupEvent ev) {
        // Dispara em background alguns segundos após subir o HTTP server
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/aerodromos/reload"))
                        .timeout(Duration.ofSeconds(30))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                client.send(req, HttpResponse.BodyHandlers.discarding());
            } catch (Exception ignore) {
            }
        }, "reload-trigger").start();
    }
}


