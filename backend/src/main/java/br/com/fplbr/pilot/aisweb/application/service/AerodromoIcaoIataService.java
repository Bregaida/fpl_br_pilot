package br.com.fplbr.pilot.aisweb.application.service;

import br.com.fplbr.pilot.aisweb.application.dto.AerodromoIcaoIataDto;
import br.com.fplbr.pilot.aisweb.domain.enums.AerodromoIcaoIataEnum;
import br.com.fplbr.pilot.aisweb.infrastructure.persistence.entity.AerodromoIcaoIataEntity;
import br.com.fplbr.pilot.aisweb.infrastructure.persistence.repository.AerodromoIcaoIataRepository;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para consultar API AISWEB e gerenciar dados de aeródromos ICAO/IATA
 */
@ApplicationScoped
public class AerodromoIcaoIataService {
    
    @Inject
    AerodromoIcaoIataRepository repository;
    
    @ConfigProperty(name = "aisweb.api.key")
    String apiKey;
    
    @ConfigProperty(name = "aisweb.api.pass")
    String apiPass;
    
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    
    /**
     * Consulta a API AISWEB e atualiza o banco de dados com todos os aeródromos
     */
    @Transactional
    public List<AerodromoIcaoIataDto> consultarEAtualizarAerodromos() {
        try {
            System.out.println("🔄 Iniciando consulta à API AISWEB...");
            
            // Consultar API AISWEB
            String xmlResponse = consultarApiAisweb();
            System.out.println("📡 Resposta da API recebida: " + (xmlResponse != null ? xmlResponse.length() + " caracteres" : "null"));
            
            // Parsear XML e extrair dados
            List<AerodromoIcaoIataEntity> aerodromos = parsearXmlResponse(xmlResponse);
            System.out.println("📋 Aeródromos parseados: " + aerodromos.size());
            
            // Processar em lotes para evitar problemas de conexão
            List<AerodromoIcaoIataDto> aerodromosSalvos = processarAerodromosEmLotes(aerodromos);
            
            System.out.println("✅ Total de aeródromos processados: " + aerodromosSalvos.size());
            return aerodromosSalvos;
                    
        } catch (Exception e) {
            System.err.println("❌ Erro ao consultar e atualizar aeródromos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao consultar e atualizar aeródromos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processa aeródromos em lotes para evitar problemas de conexão
     */
    private List<AerodromoIcaoIataDto> processarAerodromosEmLotes(List<AerodromoIcaoIataEntity> aerodromos) {
        List<AerodromoIcaoIataDto> aerodromosSalvos = new ArrayList<>();
        int tamanhoLote = 50; // Processar 50 aeródromos por vez
        
        for (int i = 0; i < aerodromos.size(); i += tamanhoLote) {
            int fim = Math.min(i + tamanhoLote, aerodromos.size());
            List<AerodromoIcaoIataEntity> lote = aerodromos.subList(i, fim);
            
            System.out.println("📦 Processando lote " + (i/tamanhoLote + 1) + " (aeródromos " + (i+1) + " a " + fim + ")");
            
            try {
                List<AerodromoIcaoIataDto> loteSalvo = processarLoteAerodromosComTransacao(lote);
                aerodromosSalvos.addAll(loteSalvo);
                System.out.println("✅ Lote processado: " + loteSalvo.size() + " aeródromos");
                
                // Pequena pausa entre lotes para evitar sobrecarga
                Thread.sleep(100);
                
            } catch (Exception e) {
                System.err.println("❌ Erro ao processar lote " + (i/tamanhoLote + 1) + ": " + e.getMessage());
                e.printStackTrace();
                // Continuar com o próximo lote mesmo se um falhar
            }
        }
        
        return aerodromosSalvos;
    }
    
    /**
     * Processa um lote de aeródromos em uma transação (método público)
     */
    @Transactional
    public List<AerodromoIcaoIataDto> processarLoteAerodromosComTransacao(List<AerodromoIcaoIataEntity> lote) {
        return processarLoteAerodromos(lote);
    }
    
    /**
     * Processa um lote de aeródromos em uma transação
     */
    private List<AerodromoIcaoIataDto> processarLoteAerodromos(List<AerodromoIcaoIataEntity> lote) {
        List<AerodromoIcaoIataDto> aerodromosSalvos = new ArrayList<>();
        
        for (AerodromoIcaoIataEntity aerodromo : lote) {
            try {
                System.out.println("💾 Processando aeródromo: " + aerodromo.getIcao() + " - " + aerodromo.getNomeAerodromo());
                AerodromoIcaoIataEntity aerodromoSalvo = salvarOuAtualizarAerodromo(aerodromo);
                aerodromosSalvos.add(converterParaDto(aerodromoSalvo));
            } catch (Exception e) {
                System.err.println("❌ Erro ao salvar aeródromo " + aerodromo.getIcao() + ": " + e.getMessage());
                // Continuar com o próximo aeródromo mesmo se um falhar
            }
        }
        
        return aerodromosSalvos;
    }
    
    /**
     * Consulta a API AISWEB para obter dados de todos os aeródromos
     */
    public String consultarApiAisweb() throws Exception {
        try {
            // Primeiro, fazer uma chamada para obter o total de registros
            String totalUrl = String.format(
                "https://aisweb.decea.mil.br/api/?apiKey=%s&apiPass=%s&area=rotaer&rowstart=0&rowend=1",
                apiKey, apiPass
            );
            
            System.out.println("🔗 URL para obter total: " + totalUrl);
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest totalRequest = HttpRequest.newBuilder()
                .uri(URI.create(totalUrl))
                .GET()
                .build();
            
            HttpResponse<String> totalResponse = client.send(totalRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            System.out.println("📡 Status da resposta (total): " + totalResponse.statusCode());
            
            // Extrair o total de registros do XML
            int totalRegistros = extrairTotalRegistros(totalResponse.body());
            System.out.println("📊 Total de registros encontrados: " + totalRegistros);
            
            if (totalRegistros <= 0) {
                System.err.println("⚠️ Total de registros inválido, usando dados de exemplo");
                return gerarDadosExemplo();
            }
            
            // Agora fazer a chamada completa com todos os registros
            String fullUrl = String.format(
                "https://aisweb.decea.mil.br/api/?apiKey=%s&apiPass=%s&area=rotaer&rowstart=0&rowend=%d",
                apiKey, apiPass, totalRegistros
            );
            
            System.out.println("🔗 URL completa: " + fullUrl);
            
            HttpRequest fullRequest = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();
            
            HttpResponse<String> fullResponse = client.send(fullRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            System.out.println("📡 Status da resposta (completa): " + fullResponse.statusCode());
            System.out.println("📄 Tamanho da resposta: " + fullResponse.body().length() + " caracteres");
            System.out.println("📄 Primeiros 200 caracteres: " + fullResponse.body().substring(0, Math.min(200, fullResponse.body().length())));
            
            return fullResponse.body();
                
        } catch (Exception e) {
            // Log do erro e fallback para dados de exemplo em caso de falha
            System.err.println("❌ Erro ao consultar API AISWEB: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar dados de exemplo para desenvolvimento
            return gerarDadosExemplo();
        }
    }
    
    /**
     * Extrai o total de registros do XML de resposta
     */
    private int extrairTotalRegistros(String xml) {
        try {
            String cleanXml = limparXml(xml);
            Document doc = XmlUtils.createDocument(cleanXml);
            
            // Buscar o atributo total no elemento rotaer
            NodeList rotaerNodes = (NodeList) XPATH.evaluate("//rotaer", doc, XPathConstants.NODESET);
            if (rotaerNodes.getLength() > 0) {
                Element rotaer = (Element) rotaerNodes.item(0);
                String totalStr = rotaer.getAttribute("total");
                if (totalStr != null && !totalStr.trim().isEmpty()) {
                    return Integer.parseInt(totalStr.trim());
                }
            }
            
            System.err.println("⚠️ Não foi possível extrair o total de registros do XML");
            return 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao extrair total de registros: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Parseia o XML de resposta da API AISWEB
     */
    public List<AerodromoIcaoIataEntity> parsearXmlResponse(String xml) throws Exception {
        List<AerodromoIcaoIataEntity> aerodromos = new ArrayList<>();
        
        try {
            System.out.println("🔍 Iniciando parsing do XML...");
            
            // Limpar XML de possíveis problemas
            String cleanXml = limparXml(xml);
            System.out.println("🧹 XML limpo: " + cleanXml.length() + " caracteres");
            
            Document doc = XmlUtils.createDocument(cleanXml);
        NodeList items = (NodeList) XPATH.evaluate("//item", doc, XPathConstants.NODESET);
            
            System.out.println("📦 Itens encontrados no XML: " + items.getLength());
        
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            
                try {
                    // Extrair ciad_id do atributo do elemento item
                    String ciadIdStr = item.getAttribute("ciad_id");
                    Long ciadId = parseLongSafely(ciadIdStr);
                    
            String ciad = getTextContent(item, "ciad");
            String tipoAerodromo = getTextContent(item, "type");
            String icao = getTextContent(item, "AeroCode");
            String nomeAerodromo = getTextContent(item, "name");
            String cidadeAerodromo = getTextContent(item, "city");
            String ufAerodromo = getTextContent(item, "uf");
            LocalDateTime dataPublicacao = parseDateTime(getTextContent(item, "dt"));
                    
                    System.out.println("📋 Processando item " + i + ": ICAO=" + icao + ", Nome=" + nomeAerodromo + ", CIAD_ID=" + ciadId);
                    
                    // Validar dados obrigatórios
                    if (icao == null || icao.trim().isEmpty()) {
                        System.err.println("⚠️ Aeródromo ignorado: ICAO vazio");
                        continue;
                    }
                    
                    if (ciadId == null) {
                        System.err.println("⚠️ Aeródromo ignorado: CIAD_ID vazio");
                        continue;
                    }
            
            // Buscar IATA no enum
            String iata = buscarIataNoEnum(icao);
            
            AerodromoIcaoIataEntity aerodromo = new AerodromoIcaoIataEntity(
                ciadId, ciad, tipoAerodromo, icao, iata, 
                nomeAerodromo, cidadeAerodromo, ufAerodromo, dataPublicacao
            );
            
            aerodromos.add(aerodromo);
                    System.out.println("✅ Item " + i + " processado com sucesso");
                    
                } catch (Exception e) {
                    System.err.println("❌ Erro ao processar item " + i + ": " + e.getMessage());
                    e.printStackTrace();
                    // Continuar processando outros itens
                }
            }
            
            System.out.println("🎯 Total de aeródromos parseados: " + aerodromos.size());
            
        } catch (Exception e) {
            System.err.println("❌ Erro no parsing XML: " + e.getMessage());
            e.printStackTrace();
            // Retornar lista vazia em caso de erro
        }
        
        return aerodromos;
    }
    
    /**
     * Limpa o XML removendo elementos mal formados
     */
    private String limparXml(String xml) {
        if (xml == null) return "";
        
        // Remover elementos auto-fechados problemáticos
        xml = xml.replaceAll("<hr[^>]*>", "");
        xml = xml.replaceAll("<br[^>]*>", "");
        xml = xml.replaceAll("<img[^>]*>", "");
        
        // Garantir que o XML tenha uma estrutura válida
        if (!xml.trim().startsWith("<")) {
            xml = "<aisweb>" + xml + "</aisweb>";
        }
        
        return xml;
    }
    
    /**
     * Converte String para Long de forma segura
     */
    private Long parseLongSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Gera dados de exemplo para desenvolvimento
     */
    private String gerarDadosExemplo() {
        return """
            <aisweb>
                <item>
                    <ciad_id>1</ciad_id>
                    <id>1</id>
                    <type>aeródromo</type>
                    <AeroCode>SBSP</AeroCode>
                    <ciad>SP0001</ciad>
                    <name>Aeroporto de Congonhas</name>
                    <city>São Paulo</city>
                    <uf>SP</uf>
                    <dt>2024-01-01T00:00:00Z</dt>
                </item>
                <item>
                    <ciad_id>2</ciad_id>
                    <id>2</id>
                    <type>aeródromo</type>
                    <AeroCode>SBGR</AeroCode>
                    <ciad>SP0002</ciad>
                    <name>Aeroporto Internacional de Guarulhos</name>
                    <city>Guarulhos</city>
                    <uf>SP</uf>
                    <dt>2024-01-01T00:00:00Z</dt>
                </item>
                <item>
                    <ciad_id>3</ciad_id>
                    <id>3</id>
                    <type>aeródromo</type>
                    <AeroCode>SBGO</AeroCode>
                    <ciad>GO0001</ciad>
                    <name>Aeroporto de Goiânia</name>
                    <city>Goiânia</city>
                    <uf>GO</uf>
                    <dt>2024-01-01T00:00:00Z</dt>
                </item>
            </aisweb>
            """;
    }
    
    /**
     * Busca o código IATA no enum baseado no ICAO
     */
    private String buscarIataNoEnum(String icao) {
        AerodromoIcaoIataEnum aerodromoEnum = AerodromoIcaoIataEnum.findByIcao(icao);
        return aerodromoEnum != null ? aerodromoEnum.getIata() : null;
    }
    
    /**
     * Salva ou atualiza um aeródromo no banco
     */
    private AerodromoIcaoIataEntity salvarOuAtualizarAerodromo(AerodromoIcaoIataEntity aerodromo) {
        try {
            // Tentar buscar aeródromo existente
            AerodromoIcaoIataEntity existente = repository.findByIcao(aerodromo.getIcao());
            
            if (existente != null) {
                // Atualizar aeródromo existente
                existente.setCiadId(aerodromo.getCiadId());
                existente.setCiad(aerodromo.getCiad());
                existente.setTipoAerodromo(aerodromo.getTipoAerodromo());
                existente.setIata(aerodromo.getIata());
                existente.setNomeAerodromo(aerodromo.getNomeAerodromo());
                existente.setCidadeAerodromo(aerodromo.getCidadeAerodromo());
                existente.setUfAerodromo(aerodromo.getUfAerodromo());
                existente.setDataPublicacao(aerodromo.getDataPublicacao());
                existente.setDataAtualizacao(LocalDateTime.now());
                
                // Usar merge em vez de persist para atualização
                return repository.getEntityManager().merge(existente);
            } else {
                // Salvar novo aeródromo
                aerodromo.setDataAtualizacao(LocalDateTime.now());
                repository.persist(aerodromo);
                return aerodromo;
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar/atualizar aeródromo " + aerodromo.getIcao() + ": " + e.getMessage());
            // Em caso de erro, tentar apenas salvar como novo
            try {
                aerodromo.setDataAtualizacao(LocalDateTime.now());
                repository.persist(aerodromo);
                return aerodromo;
            } catch (Exception e2) {
                System.err.println("❌ Erro crítico ao salvar aeródromo " + aerodromo.getIcao() + ": " + e2.getMessage());
                throw e2;
            }
        }
    }
    
    /**
     * Converte entidade para DTO
     */
    private AerodromoIcaoIataDto converterParaDto(AerodromoIcaoIataEntity entity) {
        return new AerodromoIcaoIataDto(
            entity.getId(),
            entity.getCiadId(),
            entity.getCiad(),
            entity.getTipoAerodromo(),
            entity.getIcao(),
            entity.getIata(),
            entity.getNomeAerodromo(),
            entity.getCidadeAerodromo(),
            entity.getUfAerodromo(),
            entity.getDataAtualizacao(),
            entity.getDataPublicacao()
        );
    }
    
    /**
     * Busca aeródromos com filtros opcionais
     * @param icao Código ICAO (opcional)
     * @param iata Código IATA (opcional)
     * @param uf Sigla da UF (opcional)
     * @return Lista de aeródromos filtrados
     */
    public List<AerodromoIcaoIataDto> buscarAerodromos(String icao, String iata, String uf) {
        if (icao != null && !icao.trim().isEmpty()) {
            // Busca específica por ICAO
            AerodromoIcaoIataEntity entity = repository.findByIcao(icao.toUpperCase());
            if (entity != null) {
                // Validar se o ICAO solicitado corresponde ao ICAO do registro
                if (icao.toUpperCase().equals(entity.getIcao())) {
                    return List.of(converterParaDto(entity));
                } else {
                    // ICAO não corresponde, retornar null para IATA
                    AerodromoIcaoIataDto dto = converterParaDto(entity);
                    // Criar novo DTO com IATA null
                    return List.of(new AerodromoIcaoIataDto(
                        dto.id(), dto.ciadId(), dto.ciad(), dto.tipoAerodromo(), 
                        dto.icao(), null, // IATA = null quando ICAO não corresponde
                        dto.nomeAerodromo(), dto.cidadeAerodromo(), 
                        dto.ufAerodromo(), dto.dataAtualizacao(), dto.dataPublicacao()
                    ));
                }
            }
            return List.of();
        } else if (iata != null && !iata.trim().isEmpty()) {
            // Busca específica por IATA
            AerodromoIcaoIataEntity entity = repository.findByIata(iata.toUpperCase());
            return entity != null ? List.of(converterParaDto(entity)) : List.of();
        } else if (uf != null && !uf.trim().isEmpty()) {
            // Busca por UF
            return repository.findByUf(uf.toUpperCase()).stream()
                    .map(this::converterParaDto)
                    .toList();
        } else {
            // Lista todos
            return repository.listAll().stream()
                    .map(this::converterParaDto)
                    .toList();
        }
    }
    
    /**
     * Lista todos os aeródromos do banco
     */
    public List<AerodromoIcaoIataDto> listarTodos() {
        return repository.listAll().stream()
                .map(this::converterParaDto)
                .toList();
    }
    
    /**
     * Busca aeródromo por ICAO
     */
    public AerodromoIcaoIataDto buscarPorIcao(String icao) {
        AerodromoIcaoIataEntity entity = repository.findByIcao(icao);
        return entity != null ? converterParaDto(entity) : null;
    }
    
    /**
     * Busca aeródromo por IATA
     */
    public AerodromoIcaoIataDto buscarPorIata(String iata) {
        AerodromoIcaoIataEntity entity = repository.findByIata(iata);
        return entity != null ? converterParaDto(entity) : null;
    }
    
    /**
     * Busca aeródromos por UF
     */
    public List<AerodromoIcaoIataDto> buscarPorUf(String uf) {
        return repository.findByUf(uf).stream()
                .map(this::converterParaDto)
                .toList();
    }
    
    // Métodos auxiliares
    private String getTextContent(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            // Assumindo formato ISO 8601
            return LocalDateTime.parse(dateTimeStr.replace("Z", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
