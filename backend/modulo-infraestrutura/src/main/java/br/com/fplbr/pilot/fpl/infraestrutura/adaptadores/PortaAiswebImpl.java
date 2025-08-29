package br.com.fplbr.pilot.fpl.infraestrutura.adaptadores;

import br.com.fplbr.pilot.fpl.aplicacao.dto.Aerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.CartaAerodromo;
import br.com.fplbr.pilot.fpl.aplicacao.dto.Notam;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaAisweb;
import br.com.fplbr.pilot.fpl.infraestrutura.clientes.aisweb.ClienteAiswebApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PortaAiswebImpl implements PortaAisweb {

    @RestClient
    ClienteAiswebApi api;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Notam> listarNotams(String icao) {
        String json = api.listarNotams(icao);
        List<Notam> saida = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(json);
            // aceita array ou objeto {items:[..]}
            JsonNode itens = root.isArray() ? root : root.path("items");
            if (itens != null && itens.isArray()) {
                for (JsonNode n : itens) {
                    String id = pick(n, "id", "idNotam", "numero", "codigo");
                    String texto = pick(n, "texto", "conteudo", "mensagem", "notam", "descricao");
                    if (id == null || id.isBlank()) id = "S/ID";
                    if (texto == null || texto.isBlank()) texto = n.toString();
                    saida.add(new Notam(id, texto));
                }
            } else {
                // fallback único
                String id = pick(root, "id", "idNotam", "numero", "codigo");
                String texto = pick(root, "texto", "conteudo", "mensagem", "notam", "descricao");
                saida.add(new Notam(id != null ? id : "S/ID", texto != null ? texto : root.toString()));
            }
        } catch (Exception e) {
            saida.add(new Notam("ERRO", "Falha ao interpretar NOTAM: " + e.getMessage()));
        }
        return saida;
    }

    @Override
    public List<CartaAerodromo> listarCartas(String icao) {
        String json = api.listarCartas(icao);
        List<CartaAerodromo> saida = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode itens = root.isArray() ? root : root.path("items");
            if (itens != null && itens.isArray()) {
                for (JsonNode c : itens) {
                    String tipo = pick(c, "tipo", "tipoCarta", "categoria", "classificacao");
                    String titulo = pick(c, "titulo", "title", "nome");
                    String url = pick(c, "url", "link", "download");
                    if (tipo == null) tipo = "DESCONHECIDO";
                    if (titulo == null) titulo = "Sem título";
                    if (url == null) url = "";
                    saida.add(new CartaAerodromo(tipo, titulo, url));
                }
            }
        } catch (Exception e) {
            saida.add(new CartaAerodromo("ERRO", "Falha ao interpretar cartas: " + e.getMessage(), ""));
        }
        return saida;
    }

    @Override
    public Aerodromo obterAerodromo(String icao) {
        String json = api.obterAerodromo(icao);
        try {
            JsonNode a = mapper.readTree(json);
            // aceita objeto direto ou raiz {data:{..}}
            JsonNode n = a.has("data") ? a.get("data") : a;
            String nome = pick(n, "nome", "aeroporto", "aerodromo", "designacao");
            String municipio = pick(n, "municipio", "cidade", "localidade");
            String uf = pick(n, "uf", "estado", "provincia", "ufSigla");
            Double lat = pickDouble(n, "latitude", "lat");
            Double lon = pickDouble(n, "longitude", "lon", "long");
            return new Aerodromo(
                    pick(n, "icao", "codigoIcao", "codIcao", "id", "sigla") != null ? pick(n, "icao", "codigoIcao", "codIcao", "id", "sigla") : icao,
                    nome != null ? nome : "Desconhecido",
                    municipio != null ? municipio : "",
                    uf != null ? uf : "",
                    lat, lon
            );
        } catch (Exception e) {
            return new Aerodromo(icao, "Erro ao obter aeródromo: " + e.getMessage(), "", "", null, null);
        }
    }

    private static String pick(JsonNode n, String.. keys) {
        for (String k : keys) {
            if (n.has(k) && !n.get(k).isNull()) {
                String v = n.get(k).asText();
                if (v != null && !v.isBlank()) return v;
            }
        }
        return null;
    }

    private static Double pickDouble(JsonNode n, String.. keys) {
        for (String k : keys) {
            if (n.has(k) && n.get(k).isNumber()) return n.get(k).asDouble();
            if (n.has(k) && n.get(k).isTextual()) {
                try { return Double.parseDouble(n.get(k).asText()); } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
