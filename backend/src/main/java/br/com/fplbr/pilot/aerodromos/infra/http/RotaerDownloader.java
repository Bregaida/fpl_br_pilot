package br.com.fplbr.pilot.aerodromos.infra.http;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@ApplicationScoped
public class RotaerDownloader {
    private static final String ROTAER_PDF = "https://aisweb.decea.gov.br/downloads/rotaer/rotaer_completo.pdf";
    private static final Logger LOG = Logger.getLogger(RotaerDownloader.class);

    public byte[] baixarPdf() {
        try {
            URL url = new URL(ROTAER_PDF);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(120000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "FPLBR/1.0 (+https://localhost)");
            conn.setRequestProperty("Accept", "application/pdf,*/*");
            conn.setRequestProperty("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8");
            int code = conn.getResponseCode();
            if (code != 200) {
                LOG.warnf("Falha ao baixar ROTAER PDF. HTTP %d", code);
                return new byte[0];
            }
            try (InputStream in = conn.getInputStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            LOG.error("Erro ao baixar ROTAER PDF", e);
            return new byte[0];
        }
    }
}


