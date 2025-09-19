package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.infrastructure.util.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para dados ROTAER do AISWEB usando XPath.
 * 
 * Mapeia campos conforme XML real da API AISWEB:
 * - Coordenadas: lat, lng
 * - Dados básicos: AeroCode, name, city, uf, org/name
 * - Pistas: runways/runway com type, ident, surface, length, width
 * - Serviços: services/service com COM, combustível, MET, AIS
 * - Observações: rmk/rmkText com cod e texto
 * - Distâncias: rmkDistDeclared/rmkDist com rwy, tora, toda, asda, lda
 */
@ApplicationScoped
public class RotaerParser {

    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    public RotaerDto parse(String xml, String requestedIcao) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
            Element root = doc.getDocumentElement();
        root.normalize();
        
        // Filtrar pelo ICAO solicitado
        Element ad = selectOne(root, "//aisweb[AeroCode = '" + requestedIcao + "']");
        if (ad == null) {
            // Retornar DTO vazio mas consistente
            return createEmptyRotaerDto(requestedIcao);
        }
        
        return new RotaerDto(
            toDouble(text(ad, "lat")),
            toDouble(text(ad, "lng")),
            text(ad, "org/name"),
            text(ad, "AeroCode"),
            text(ad, "name"),
            text(ad, "city"),
            text(ad, "uf"),
            null, // IATA não está no XML
            toInt(text(ad, "altM")),
            // Novos campos da API AISWEB
            text(ad, "ciad"),
            text(ad, "typeUtil"),
            text(ad, "typeOpr"),
            text(ad, "utc"),
            toInt(text(ad, "altFt")),
            parseFrequenciaOperacao(ad),
            parseIluminacao(ad),
            parsePistas(ad),
            parseServicos(ad),
            parseObservacoes(ad),
            parseDistanciasDeclaradas(ad),
            parseComplementos(ad),
            parseDataPublicacao(ad),
            parseQtdePistas(ad),
            parseQtdeObservacoes(ad)
        );
    }

    private RotaerDto createEmptyRotaerDto(String icao) {
        return new RotaerDto(
            null, null, null, icao, null, null, null, null, null,
            // Novos campos da API AISWEB
            null, null, null, null, null,
            false, new ArrayList<>(), new ArrayList<>(), new ServicosDto(
                new ArrayList<>(), null, null, null, null, null, null
            ), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            null, 0, 0
        );
    }

    // Helpers XPath
    private static Element asElement(Node n) {
        return (n != null && n.getNodeType() == Node.ELEMENT_NODE) ? (Element) n : null;
    }

    private static NodeList select(Element base, String expr) throws XPathExpressionException {
        return (NodeList) XPATH.evaluate(expr, base, XPathConstants.NODESET);
    }

    private static Element selectOne(Element base, String expr) throws XPathExpressionException {
        Node n = (Node) XPATH.evaluate(expr, base, XPathConstants.NODE);
        return asElement(n);
    }

    private static String text(Element base, String expr) throws XPathExpressionException {
        String v = (String) XPATH.evaluate(expr, base, XPathConstants.STRING);
        return (v != null && !v.isBlank()) ? v.trim() : null;
    }

    // Conversores
    private static Double toDouble(String s) {
        try {
            return (s == null) ? null : Double.valueOf(s.replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer toInt(String s) {
        try {
            return (s == null) ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }


    private Boolean parseFrequenciaOperacao(Element ad) {
        try {
            // Verificar se há timesheets com horários específicos
            NodeList timesheets = select(ad, "timesheets/timesheet");
            if (timesheets.getLength() > 0) {
                // Se há timesheets, verificar se opera 24h
                for (int i = 0; i < timesheets.getLength(); i++) {
                    Element ts = asElement(timesheets.item(i));
                    if (ts != null) {
                        String begin = text(ts, "hours/begin");
                        String end = text(ts, "hours/end");
                        if ("00:00".equals(begin) && "23:59".equals(end)) {
                            return true;
                        }
                    }
                }
                return false; // Opera em horários específicos
            }
            
            // Se não há timesheets, assumir 24x7
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<IluminacaoAerodromoDto> parseIluminacao(Element ad) {
        List<IluminacaoAerodromoDto> iluminacoes = new ArrayList<>();
        try {
            NodeList lights = select(ad, "lights/light");
            for (int i = 0; i < lights.getLength(); i++) {
                Element light = asElement(lights.item(i));
                if (light == null) continue;
                
                String codigo = light.getTextContent();
                String descricao = light.getAttribute("descr");
                String compl = light.getAttribute("compl");
                
            iluminacoes.add(new IluminacaoAerodromoDto(
                    codigo != null ? codigo.trim() : null,
                    descricao != null && !descricao.isBlank() ? descricao : null,
                    resolveCompl(compl, ad)
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return iluminacoes;
    }

    private List<PistaDto> parsePistas(Element ad) {
        List<PistaDto> pistas = new ArrayList<>();
        try {
            NodeList runways = select(ad, "runways/runway");
            for (int i = 0; i < runways.getLength(); i++) {
                Element runway = asElement(runways.item(i));
                if (runway == null) continue;
                
            pistas.add(new PistaDto(
                    text(runway, "type"),
                    text(runway, "ident"),
                    text(runway, "surface"),
                    toInt(text(runway, "length")),
                    toInt(text(runway, "width")),
                    text(runway, "surface_c"),
                parseLuzesPista(runway),
                parseCabeceiras(runway)
            ));
        }
        } catch (Exception e) {
            // Log error but continue
        }
        return pistas;
    }

    private List<IluminacaoAerodromoDto> parseLuzesPista(Element runway) {
        List<IluminacaoAerodromoDto> luzes = new ArrayList<>();
        try {
            NodeList lights = select(runway, "lights/light");
            for (int i = 0; i < lights.getLength(); i++) {
                Element light = asElement(lights.item(i));
                if (light == null) continue;
                
                String codigo = light.getTextContent();
                String descricao = light.getAttribute("descr");
                String compl = light.getAttribute("compl");
                
                luzes.add(new IluminacaoAerodromoDto(
                    codigo != null ? codigo.trim() : null,
                    descricao != null && !descricao.isBlank() ? descricao : null,
                    resolveCompl(compl, runway)
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return luzes;
    }

    private List<CabeceiraDto> parseCabeceiras(Element runway) {
        List<CabeceiraDto> cabeceiras = new ArrayList<>();
        try {
            NodeList thresholds = select(runway, "thr");
            for (int i = 0; i < thresholds.getLength(); i++) {
                Element thr = asElement(thresholds.item(i));
                if (thr == null) continue;
                
                cabeceiras.add(new CabeceiraDto(
                    text(thr, "ident"),
                    parseLuzesPista(thr)
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return cabeceiras;
    }

    private ServicosDto parseServicos(Element ad) {
        try {
            Element serv = selectOne(ad, "services");
            if (serv == null) {
                return new ServicosDto(new ArrayList<>(), null, null, null, null, null, null);
            }
            
            return new ServicosDto(
                parseComunicacoes(serv),
                parseCombustivel(serv),
                parseServicoAeronave(serv),
                parseServicoAeroporto(serv),
                parseMet(serv),
                parseAis(serv),
                parseNav(serv)
            );
        } catch (Exception e) {
            return new ServicosDto(new ArrayList<>(), null, null, null, null, null, null);
        }
    }

    private List<ComunicacaoDto> parseComunicacoes(Element serv) {
        List<ComunicacaoDto> comunicacoes = new ArrayList<>();
        try {
            NodeList coms = select(serv, "service[@type='COM']");
            for (int i = 0; i < coms.getLength(); i++) {
                Element com = asElement(coms.item(i));
                if (com == null) continue;
                
                comunicacoes.add(new ComunicacaoDto(
                    text(com, "type"),
                    text(com, "callsign"),
                    parseFrequencias(com)
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return comunicacoes;
    }

    private List<String> parseFrequencias(Element com) {
        List<String> frequencias = new ArrayList<>();
        try {
            NodeList freqs = select(com, "freqs/freq");
            for (int i = 0; i < freqs.getLength(); i++) {
                Element freq = asElement(freqs.item(i));
                if (freq == null) continue;
                
                String freqText = freq.getTextContent();
                if (freqText != null && !freqText.isBlank()) {
                    frequencias.add(freqText.trim());
                }
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return frequencias;
    }

    private String parseCombustivel(Element serv) {
        try {
            return text(serv, "service[@type='AirportSuppliesService']/fuel/span");
        } catch (Exception e) {
            return null;
        }
    }

    private String parseServicoAeronave(Element serv) {
        try {
            return text(serv, "service[@type='AircraftGroundService']/ser");
        } catch (Exception e) {
            return null;
        }
    }

    private String parseServicoAeroporto(Element serv) {
        try {
            return text(serv, "service[@type='AirportGroundService']/rffs");
        } catch (Exception e) {
            return null;
        }
    }

    private String parseMet(Element serv) {
        try {
            return text(serv, "service[@type='MET']/type");
        } catch (Exception e) {
            return null;
        }
    }

    private String parseAis(Element serv) {
        try {
            return text(serv, "service[@type='AIS']/type");
        } catch (Exception e) {
            return null;
        }
    }

    private String parseNav(Element serv) {
        try {
            return text(serv, "service[@type='NAV']/type");
        } catch (Exception e) {
            return null;
        }
    }

    private List<ObservacaoDto> parseObservacoes(Element ad) {
        List<ObservacaoDto> observacoes = new ArrayList<>();
        try {
            NodeList obs = select(ad, "rmk/rmkText");
            for (int i = 0; i < obs.getLength(); i++) {
                Element ob = asElement(obs.item(i));
                if (ob == null) continue;
                
                String codigo = ob.getAttribute("cod");
                String texto = ob.getTextContent();
                
                // Limpar o texto e mapear códigos para categorias legíveis
                if (texto != null) {
                    texto = texto.trim()
                        .replaceAll("\\s+", " ") // Normalizar espaços
                        .replaceAll("<br>", "\n") // Converter quebras de linha
                        .replaceAll("\\[CDATA\\[", "") // Remover CDATA
                        .replaceAll("\\]\\]", ""); // Remover fechamento CDATA
                }
                
                // Mapear códigos para categorias legíveis
                String categoria = mapObservationCode(codigo);
                
                observacoes.add(new ObservacaoDto(
                    codigo, // Manter o código original
                    texto
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return observacoes;
    }
    
    private String mapObservationCode(String codigo) {
        if (codigo == null) return null;
        
        switch (codigo) {
            case "2.2": return "DADOS GEOGRÁFICOS E ADMINISTRATIVOS";
            case "2.3": return "HORÁRIO DE FUNCIONAMENTO";
            case "2.6": return "SERVIÇOS DE SALVAMENTO E COMBATE A INCÊNDIO";
            case "2.8": return "DADOS DE PÁTIOS, PISTAS DE TÁXI E PONTOS DE VERIFICAÇÃO";
            case "2.10": return "OBSTÁCULOS DE AERÓDROMO";
            case "2.12": return "CARACTERÍSTICAS FÍSICAS DA PISTA";
            case "2.19": return "AUXÍLIOS-RÁDIO A NAVEGAÇÃO E POUSO";
            case "2.20": return "REGULAMENTOS LOCAIS DE AERÓDROMO";
            case "2.22": return "PROCEDIMENTOS DE VOO";
            case "2.23": return "INFORMAÇÃO ADICIONAL";
            default: return "OBSERVAÇÃO " + codigo;
        }
    }

    private List<DistanciaDeclaradaDto> parseDistanciasDeclaradas(Element ad) {
        List<DistanciaDeclaradaDto> distancias = new ArrayList<>();
        try {
            NodeList rwys = select(ad, "rmkDistDeclared/rmkDist");
            for (int i = 0; i < rwys.getLength(); i++) {
                Element rwy = asElement(rwys.item(i));
                if (rwy == null) continue;
                
                distancias.add(new DistanciaDeclaradaDto(
                    text(rwy, "rwy"),
                    toInt(text(rwy, "tora")),
                    toInt(text(rwy, "toda")),
                    toInt(text(rwy, "asda")),
                    toInt(text(rwy, "lda")),
                    toDouble(text(rwy, "lat")),
                    toDouble(text(rwy, "lng"))
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return distancias;
    }

    private List<ComplementoDto> parseComplementos(Element ad) {
        List<ComplementoDto> complementos = new ArrayList<>();
        try {
            NodeList compls = select(ad, "compls/compl");
            for (int i = 0; i < compls.getLength(); i++) {
                Element compl = asElement(compls.item(i));
                if (compl == null) continue;
                
                String codigo = compl.getAttribute("cod");
                String texto = compl.getTextContent();
                
                // Limpar o texto
                if (texto != null) {
                    texto = texto.trim()
                        .replaceAll("\\s+", " ") // Normalizar espaços
                        .replaceAll("<br>", "\n") // Converter quebras de linha
                        .replaceAll("\\[CDATA\\[", "") // Remover CDATA
                        .replaceAll("\\]\\]", ""); // Remover fechamento CDATA
                }
                
                // Mapear códigos para categorias legíveis
                String categoria = mapComplementCode(codigo);
                
                complementos.add(new ComplementoDto(
                    codigo, // Manter o código original
                    texto
                ));
            }
        } catch (Exception e) {
            // Log error but continue
        }
        return complementos;
    }
    
    private String mapComplementCode(String codigo) {
        if (codigo == null) return null;
        
        switch (codigo) {
            case "2.16": return "COORDENADAS E OPERAÇÕES";
            case "2.14": return "ALTITUDE MÍNIMA";
            case "2.20": return "REGULAMENTOS LOCAIS";
            case "2.3": return "HORÁRIO DE FUNCIONAMENTO";
            case "2.23": return "INFORMAÇÕES ADICIONAIS";
            case "2.6": return "SERVIÇOS DE SALVAMENTO";
            default: return "COMPLEMENTO " + codigo;
        }
    }

    private LocalDateTime parseDataPublicacao(Element ad) {
        try {
            String dt = text(ad, "dt");
            return dt != null ? TsUtils.parseTs(dt) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseQtdePistas(Element ad) {
        try {
            NodeList runways = select(ad, "runways/runway");
            return runways.getLength();
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer parseQtdeObservacoes(Element ad) {
        try {
            NodeList obs = select(ad, "rmk/rmkText");
            return obs.getLength();
        } catch (Exception e) {
            return 0;
        }
    }

    private String resolveCompl(String complRef, Element context) {
        if (complRef == null || complRef.isEmpty()) {
            return null;
        }
        
        try {
            // Buscar complemento por código
            NodeList compls = select(context, "compls/compl[@cod='" + complRef + "']");
            if (compls.getLength() > 0) {
                Element compl = asElement(compls.item(0));
                if (compl != null) {
                    String texto = compl.getTextContent();
                    return texto != null ? texto.trim() : null;
                }
            }
        } catch (Exception e) {
            // Log error but continue
        }
        
        return complRef; // Retornar referência se não encontrado
    }
}

