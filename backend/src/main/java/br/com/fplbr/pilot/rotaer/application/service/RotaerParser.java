package br.com.fplbr.pilot.rotaer.application.service;

import br.com.fplbr.pilot.rotaer.domain.model.RotaerData;
import br.com.fplbr.pilot.rotaer.domain.model.ValidationWarning;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser principal do ROTAER
 * Extrai dados do texto do ROTAER usando regex robustas
 */
@ApplicationScoped
public class RotaerParser {
    
    private static final Logger log = LoggerFactory.getLogger(RotaerParser.class);
    
    @Inject
    RotaerNormalizer normalizer;
    
    // Padrões de extração
    private static final Pattern ICAO_PATTERN = Pattern.compile("^([A-Z]{4})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern COORDINATES_PATTERN = Pattern.compile("([0-9]{1,2}\\s+[0-9]{1,2}\\s+[0-9]{1,2}(?:\\.[0-9]+)?[NS]\\s*/?\\s*[0-9]{1,3}\\s+[0-9]{1,2}\\s+[0-9]{1,2}(?:\\.[0-9]+)?[EW])");
    private static final Pattern ELEVATION_PATTERN = Pattern.compile("([0-9]+)\\s*M\\s*\\(([0-9]+)\\s*FT\\)");
    private static final Pattern RUNWAY_PATTERN = Pattern.compile("RWY\\s+([0-9]{2}/[0-9]{2})\\s+([0-9]+)X([0-9]+)\\s+([A-Z]+)(?:\\s+([0-9]+/[A-Z]/[A-Z]/[A-Z]/[A-Z]))?");
    private static final Pattern FREQUENCY_PATTERN = Pattern.compile("([A-Z]+)\\s+([A-Z\\s]+)\\s+([0-9]{3}\\.[0-9]{3})(?:\\s+([0-9]{3}\\.[0-9]{3}))?");
    private static final Pattern RDONAV_PATTERN = Pattern.compile("([A-Z]+/[A-Z]+|NDB)\\s+([A-Z]{3,4})\\s+([0-9]{3}\\.[0-9])\\s+([0-9]{1,2}\\s+[0-9]{1,2}\\s+[0-9]{1,2}(?:\\.[0-9]+)?[NS]\\s*/?\\s*[0-9]{1,3}\\s+[0-9]{1,2}\\s+[0-9]{1,2}(?:\\.[0-9]+)?[EW])");
    private static final Pattern LIGHTS_PATTERN = Pattern.compile("L([0-9]+)([A-Z])?(?:\\(([0-9.]+)\\))?");
    private static final Pattern PCN_PATTERN = Pattern.compile("([0-9]+)/([RF])/([ABCD])/([WXYZ])/([TU])");
    private static final Pattern INFOTEMP_PATTERN = Pattern.compile("([FRCM])([0-9]{4})/([0-9]{4})\\s+(\\d{2}/\\d{2}/\\d{2}\\s+\\d{2}:\\d{2})\\s+(\\d{2}/\\d{2}/\\d{2}\\s+\\d{2}:\\d{2})\\s+(.+)");
    
    // Padrões para blocos numerados
    private static final Pattern BLOCK_PATTERN = Pattern.compile("^\\s*([0-9]+(?:\\.[0-9]+)?)\\s+(.+)$", Pattern.MULTILINE);
    
    /**
     * Parse principal do texto ROTAER
     */
    public RotaerData parse(String rotaerText, String icao, List<ValidationWarning> warnings) {
        if (rotaerText == null || rotaerText.trim().isEmpty()) {
            warnings.add(new ValidationWarning("rotaer_text", "null", "texto_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "Texto do ROTAER é obrigatório"));
            return null;
        }
        
        log.info("Iniciando parse do ROTAER para ICAO: {}", icao);
        
        RotaerData data = new RotaerData();
        
        // Extrair informações básicas do aeródromo
        RotaerData.AerodromoInfo aerodromo = parseAerodromo(rotaerText, icao, warnings);
        data.setAerodromo(aerodromo);
        
        // Extrair pistas
        List<RotaerData.PistaInfo> pistas = parsePistas(rotaerText, warnings);
        data.setPistas(pistas);
        
        // Extrair comunicações
        List<RotaerData.ComunicacaoInfo> comunicacoes = parseComunicacoes(rotaerText, warnings);
        data.setComunicacoes(comunicacoes);
        
        // Extrair RDONAV
        List<RotaerData.RdonavInfo> rdonav = parseRdonav(rotaerText, warnings);
        data.setRdonav(rdonav);
        
        // Extrair serviços
        RotaerData.ServicosInfo servicos = parseServicos(rotaerText, warnings);
        data.setServicos(servicos);
        
        // Extrair INFOTEMP
        List<RotaerData.InfotempInfo> infotemp = parseInfotemp(rotaerText, warnings);
        data.setInfotemp(infotemp);
        
        // Configurar fonte
        RotaerData.FonteInfo fonte = new RotaerData.FonteInfo(
            "ROTAER", icao, "texto", OffsetDateTime.now(ZoneOffset.UTC)
        );
        data.setFonte(fonte);
        
        log.info("Parse concluído para ICAO: {} - {} pistas, {} comunicações, {} rdonav", 
                icao, pistas.size(), comunicacoes.size(), rdonav.size());
        
        return data;
    }
    
    /**
     * Extrai informações básicas do aeródromo
     */
    private RotaerData.AerodromoInfo parseAerodromo(String text, String icao, List<ValidationWarning> warnings) {
        RotaerData.AerodromoInfo aerodromo = new RotaerData.AerodromoInfo();
        aerodromo.setIcao(icao);
        
        // Extrair nome do aeródromo - tentar múltiplas estratégias
        String nome = null;
        
        // Estratégia 1: Padrão ICAO + nome
        Matcher icaoMatcher = ICAO_PATTERN.matcher(text);
        if (icaoMatcher.find()) {
            nome = icaoMatcher.group(2).trim();
        }
        
        // Estratégia 2: Se não encontrou, usar o ICAO como nome
        if (nome == null || nome.isEmpty()) {
            nome = icao;
        }
        
        // Estratégia 3: Se ainda não tem nome, usar um padrão
        if (nome == null || nome.isEmpty()) {
            nome = "Aeródromo " + icao;
        }
        
        aerodromo.setNome(nome);
        log.debug("Nome do aeródromo definido como: {}", nome);
        
        // Extrair coordenadas
        Matcher coordMatcher = COORDINATES_PATTERN.matcher(text);
        if (coordMatcher.find()) {
            String coordText = coordMatcher.group(1);
            RotaerData.Coordenadas coords = normalizer.normalizeCoordinates(coordText, warnings);
            aerodromo.setCoordsArp(coords);
        }
        
        // Extrair elevação
        Matcher elevMatcher = ELEVATION_PATTERN.matcher(text);
        if (elevMatcher.find()) {
            try {
                int elevacaoM = Integer.parseInt(elevMatcher.group(1));
                int elevacaoFt = Integer.parseInt(elevMatcher.group(2));
                aerodromo.setElevacaoM(elevacaoM);
                aerodromo.setElevacaoFt(elevacaoFt);
            } catch (NumberFormatException e) {
                warnings.add(new ValidationWarning("elevacao", elevMatcher.group(0), "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, "Erro ao converter elevação"));
            }
        }
        
        // Extrair tipo, categoria, utilização, etc. dos blocos numerados
        parseAerodromoBlocks(text, aerodromo, warnings);
        
        return aerodromo;
    }
    
    /**
     * Extrai informações dos blocos numerados do aeródromo
     */
    private void parseAerodromoBlocks(String text, RotaerData.AerodromoInfo aerodromo, List<ValidationWarning> warnings) {
        Matcher blockMatcher = BLOCK_PATTERN.matcher(text);
        
        while (blockMatcher.find()) {
            String blockNumber = blockMatcher.group(1);
            String blockContent = blockMatcher.group(2).trim();
            
            switch (blockNumber) {
                case "1":
                    // Nome do aeródromo
                    if (aerodromo.getNome() == null || aerodromo.getNome().equals(aerodromo.getIcao()) || aerodromo.getNome().startsWith("Aeródromo")) {
                        aerodromo.setNome(blockContent.trim());
                        log.debug("Nome do aeródromo atualizado pelo bloco 1: {}", blockContent);
                    }
                    break;
                case "2":
                    // Município
                    aerodromo.setMunicipio(blockContent);
                    break;
                case "3":
                    // UF
                    aerodromo.setUf(blockContent.trim().toUpperCase());
                    break;
                case "4":
                    // Tipo
                    aerodromo.setTipo(blockContent.trim().toUpperCase());
                    break;
                case "5":
                    // Categoria
                    aerodromo.setCategoria(blockContent.trim().toUpperCase());
                    break;
                case "6":
                    // Utilização
                    aerodromo.setUtilizacao(blockContent.trim().toUpperCase());
                    break;
                case "7":
                    // Administrador
                    aerodromo.setAdministrador(blockContent);
                    break;
                case "8":
                    // Distância e direção da cidade
                    parseDistanciaDirecao(blockContent, aerodromo, warnings);
                    break;
                case "9":
                    // Fuso horário
                    aerodromo.setFuso(blockContent.trim());
                    break;
                case "10":
                    // Operação
                    aerodromo.setOperacao(blockContent.trim().toUpperCase());
                    break;
                case "11":
                    // Luzes do aeródromo
                    List<String> luzes = normalizer.normalizeLights(blockContent, warnings);
                    aerodromo.setLuzesAerodromo(luzes);
                    break;
                case "12":
                    // Observações gerais
                    aerodromo.setObservacoesGerais(blockContent);
                    break;
                case "13":
                    // FIR
                    aerodromo.setFir(blockContent);
                    break;
                case "14":
                    // Jurisdição
                    aerodromo.setJurisdicao(blockContent);
                    break;
            }
        }
    }
    
    /**
     * Extrai distância e direção da cidade
     */
    private void parseDistanciaDirecao(String content, RotaerData.AerodromoInfo aerodromo, List<ValidationWarning> warnings) {
        // Padrão: "15 KM N DE RIO DE JANEIRO"
        Pattern distPattern = Pattern.compile("([0-9]+)\\s*KM\\s+([NSEW]{1,2})\\s+DE\\s+(.+)");
        Matcher matcher = distPattern.matcher(content.toUpperCase());
        
        if (matcher.find()) {
            try {
                int km = Integer.parseInt(matcher.group(1));
                String direcao = matcher.group(2);
                String cidade = matcher.group(3);
                
                RotaerData.DistanciaDirecao distDir = new RotaerData.DistanciaDirecao(km, direcao);
                aerodromo.setDistanciaDirecaoCidade(distDir);
            } catch (NumberFormatException e) {
                warnings.add(new ValidationWarning("distancia_direcao", content, "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, "Erro ao converter distância"));
            }
        }
    }
    
    /**
     * Extrai informações das pistas
     */
    private List<RotaerData.PistaInfo> parsePistas(String text, List<ValidationWarning> warnings) {
        List<RotaerData.PistaInfo> pistas = new ArrayList<>();
        
        Matcher runwayMatcher = RUNWAY_PATTERN.matcher(text);
        while (runwayMatcher.find()) {
            RotaerData.PistaInfo pista = new RotaerData.PistaInfo();
            
            // Designadores
            String designators = runwayMatcher.group(1);
            List<String> designadores = normalizer.normalizeRunwayDesignators(designators, warnings);
            pista.setDesignadores(designadores);
            
            // Dimensões
            String dimensions = runwayMatcher.group(2) + "x" + runwayMatcher.group(3);
            RotaerData.DimensoesPista dimensoes = normalizer.normalizeDimensions(dimensions, warnings);
            pista.setDimensoes(dimensoes);
            
            // Piso
            String piso = runwayMatcher.group(4);
            pista.setPiso(normalizer.normalizePiso(piso, warnings));
            
            // PCN
            if (runwayMatcher.groupCount() > 4 && runwayMatcher.group(5) != null) {
                String pcn = runwayMatcher.group(5);
                pista.setPcn(normalizer.normalizePcn(pcn, warnings));
            }
            
            pistas.add(pista);
        }
        
        return pistas;
    }
    
    /**
     * Extrai comunicações
     */
    private List<RotaerData.ComunicacaoInfo> parseComunicacoes(String text, List<ValidationWarning> warnings) {
        List<RotaerData.ComunicacaoInfo> comunicacoes = new ArrayList<>();
        
        Matcher freqMatcher = FREQUENCY_PATTERN.matcher(text);
        while (freqMatcher.find()) {
            RotaerData.ComunicacaoInfo comm = new RotaerData.ComunicacaoInfo();
            
            // Órgãos
            String orgaos = freqMatcher.group(1);
            comm.setOrgaos(orgaos);
            
            // Indicativo
            String indicativo = freqMatcher.group(2);
            comm.setIndicativo(indicativo);
            
            // Frequências
            List<Double> frequencias = new ArrayList<>();
            frequencias.add(Double.parseDouble(freqMatcher.group(3)));
            if (freqMatcher.groupCount() > 3 && freqMatcher.group(4) != null) {
                frequencias.add(Double.parseDouble(freqMatcher.group(4)));
            }
            comm.setFrequenciasMhz(frequencias);
            
            // Verificar se é emergência
            comm.setEmergencia(frequencias.contains(121.5));
            
            // Tipo baseado no órgão
            comm.setTipo(determineCommType(orgaos));
            
            comunicacoes.add(comm);
        }
        
        return comunicacoes;
    }
    
    /**
     * Determina o tipo de comunicação baseado no órgão
     */
    private String determineCommType(String orgaos) {
        String upper = orgaos.toUpperCase();
        if (upper.contains("TWR") || upper.contains("GND") || upper.contains("APP") || upper.contains("ACC")) {
            return "ATS";
        } else if (upper.contains("ATIS") || upper.contains("VOLMET")) {
            return "MET";
        } else {
            return "OUTRO";
        }
    }
    
    /**
     * Extrai RDONAV
     */
    private List<RotaerData.RdonavInfo> parseRdonav(String text, List<ValidationWarning> warnings) {
        List<RotaerData.RdonavInfo> rdonav = new ArrayList<>();
        
        Matcher rdonavMatcher = RDONAV_PATTERN.matcher(text);
        while (rdonavMatcher.find()) {
            RotaerData.RdonavInfo nav = new RotaerData.RdonavInfo();
            
            // Tipo
            String tipo = rdonavMatcher.group(1);
            nav.setTipo(tipo);
            
            // Identificador
            String ident = rdonavMatcher.group(2);
            nav.setIdent(ident);
            
            // Frequência
            try {
                double freq = Double.parseDouble(rdonavMatcher.group(3));
                nav.setFreq(freq);
            } catch (NumberFormatException e) {
                warnings.add(new ValidationWarning("rdonav_freq", rdonavMatcher.group(3), "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, "Erro ao converter frequência RDONAV"));
            }
            
            // Coordenadas
            String coordText = rdonavMatcher.group(4);
            RotaerData.Coordenadas coords = normalizer.normalizeCoordinates(coordText, warnings);
            nav.setCoords(coords);
            
            rdonav.add(nav);
        }
        
        return rdonav;
    }
    
    /**
     * Extrai serviços
     */
    private RotaerData.ServicosInfo parseServicos(String text, List<ValidationWarning> warnings) {
        RotaerData.ServicosInfo servicos = new RotaerData.ServicosInfo();
        
        // Extrair combustível
        Pattern fuelPattern = Pattern.compile("COMBUSTÍVEL\\s*:\\s*([A-Z\\s,()]+)");
        Matcher fuelMatcher = fuelPattern.matcher(text.toUpperCase());
        if (fuelMatcher.find()) {
            String fuelText = fuelMatcher.group(1);
            List<String> combustivel = Arrays.asList(fuelText.split("[,\\s]+"));
            servicos.setCombustivel(combustivel);
        }
        
        // Extrair manutenção
        Pattern maintPattern = Pattern.compile("MANUTENÇÃO\\s*:\\s*([A-Z0-9\\s,]+)");
        Matcher maintMatcher = maintPattern.matcher(text.toUpperCase());
        if (maintMatcher.find()) {
            String maintText = maintMatcher.group(1);
            List<String> manutencao = Arrays.asList(maintText.split("[,\\s]+"));
            servicos.setManutencao(manutencao);
        }
        
        // Extrair RFFS
        Pattern rffsPattern = Pattern.compile("RFFS\\s*:\\s*CAT\\s+CIVIL\\s+([0-9]+)\\s+CAT\\s+MIL\\s+([0-9]+)");
        Matcher rffsMatcher = rffsPattern.matcher(text.toUpperCase());
        if (rffsMatcher.find()) {
            try {
                int catCivil = Integer.parseInt(rffsMatcher.group(1));
                int catMil = Integer.parseInt(rffsMatcher.group(2));
                RotaerData.RffsInfo rffs = new RotaerData.RffsInfo(catCivil, catMil);
                servicos.setRffs(rffs);
            } catch (NumberFormatException e) {
                warnings.add(new ValidationWarning("rffs", rffsMatcher.group(0), "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, "Erro ao converter RFFS"));
            }
        }
        
        // Extrair MET
        Pattern metPattern = Pattern.compile("MET\\s*:\\s*CMA\\s+([0-9,\\s]+)\\s+CMM\\s+([0-9,\\s]+)\\s+TEL\\s+([0-9\\s()-]+)");
        Matcher metMatcher = metPattern.matcher(text.toUpperCase());
        if (metMatcher.find()) {
            List<String> cma = Arrays.asList(metMatcher.group(1).split("[,\\s]+"));
            List<String> cmm = Arrays.asList(metMatcher.group(2).split("[,\\s]+"));
            List<String> telefones = Arrays.asList(metMatcher.group(3).split("[,\\s]+"));
            
            RotaerData.MetInfo met = new RotaerData.MetInfo(cma, cmm, telefones);
            servicos.setMet(met);
        }
        
        // Extrair AIS
        Pattern aisPattern = Pattern.compile("AIS\\s*:\\s*TEL\\s+([0-9\\s()-]+)(?:\\s+MIL\\s+([0-9\\s()-]+))?");
        Matcher aisMatcher = aisPattern.matcher(text.toUpperCase());
        if (aisMatcher.find()) {
            List<String> telefones = Arrays.asList(aisMatcher.group(1).split("[,\\s]+"));
            List<String> mil = new ArrayList<>();
            if (aisMatcher.groupCount() > 1 && aisMatcher.group(2) != null) {
                mil = Arrays.asList(aisMatcher.group(2).split("[,\\s]+"));
            }
            
            RotaerData.AisInfo ais = new RotaerData.AisInfo(telefones, mil);
            servicos.setAis(ais);
        }
        
        // Extrair RMK
        Pattern rmkPattern = Pattern.compile("RMK\\s*:\\s*(.+?)(?=\\n\\s*[A-Z]{3,4}\\s*:|$)", Pattern.DOTALL);
        Matcher rmkMatcher = rmkPattern.matcher(text.toUpperCase());
        if (rmkMatcher.find()) {
            servicos.setRmk(rmkMatcher.group(1).trim());
        }
        
        return servicos;
    }
    
    /**
     * Extrai INFOTEMP
     */
    private List<RotaerData.InfotempInfo> parseInfotemp(String text, List<ValidationWarning> warnings) {
        List<RotaerData.InfotempInfo> infotemp = new ArrayList<>();
        
        Matcher infotempMatcher = INFOTEMP_PATTERN.matcher(text);
        while (infotempMatcher.find()) {
            RotaerData.InfotempInfo info = new RotaerData.InfotempInfo();
            
            // ID
            String natureza = infotempMatcher.group(1);
            String numero1 = infotempMatcher.group(2);
            String numero2 = infotempMatcher.group(3);
            String id = natureza + numero1 + "/" + numero2;
            info.setId(id);
            info.setNatureza(natureza);
            
            // Datas
            try {
                String inicioStr = infotempMatcher.group(4);
                String fimStr = infotempMatcher.group(5);
                
                OffsetDateTime inicio = parseInfotempDate(inicioStr, warnings);
                OffsetDateTime fim = parseInfotempDate(fimStr, warnings);
                
                info.setInicio(inicio);
                info.setFim(fim);
            } catch (Exception e) {
                warnings.add(new ValidationWarning("infotemp_datas", infotempMatcher.group(0), "conversao_falhou", 
                                                 ValidationWarning.Severity.ERROR, "Erro ao converter datas INFOTEMP"));
            }
            
            // Texto
            String texto = infotempMatcher.group(6);
            info.setTexto(texto);
            
            infotemp.add(info);
        }
        
        return infotemp;
    }
    
    /**
     * Converte data do INFOTEMP para OffsetDateTime
     */
    private OffsetDateTime parseInfotempDate(String dateStr, List<ValidationWarning> warnings) {
        try {
            // Formato: DD/MM/YY HH:MM
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
            // Assumir fuso UTC-3 (Brasil)
            return OffsetDateTime.parse(dateStr + "-03:00", formatter.withZone(ZoneOffset.of("-03:00")));
        } catch (Exception e) {
            warnings.add(new ValidationWarning("infotemp_data", dateStr, "formato_invalido", 
                                             ValidationWarning.Severity.ERROR, "Formato de data INFOTEMP inválido: " + dateStr));
            return null;
        }
    }
}
