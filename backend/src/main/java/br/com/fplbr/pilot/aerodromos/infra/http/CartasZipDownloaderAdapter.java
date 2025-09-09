package br.com.fplbr.pilot.aerodromos.infra.http;

import br.com.fplbr.pilot.aerodromos.ports.in.CartasZipDownloaderPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@ApplicationScoped
public class CartasZipDownloaderAdapter implements CartasZipDownloaderPort {
    private static final String CARTAS_ZIP_URL = "https://aisweb.decea.mil.br/api/download/zip.cfm?full=AC272CD6-866A-440A-B3FBB566EC650089&amdt=2025-10-02";

    @Override
    public byte[] baixarZip() {
        try {
            URL url = new URL(CARTAS_ZIP_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("GET");
            try (InputStream in = conn.getInputStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            return new byte[0];
        }
    }
}


