package br.com.fplbr.pilot.aisweb.infrastructure.parser;

import br.com.fplbr.pilot.aisweb.application.dto.*;
import br.com.fplbr.pilot.aisweb.infrastructure.util.TsUtils;
import br.com.fplbr.pilot.aisweb.infrastructure.util.XmlUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parser para dados INFOTEMP (Situação do Aeródromo) do AISWEB.
 * 
 * Regras principais:
 * - Mapear por <item> com campos e normalizações
 * - Datas {ts '...'} com fallbacks; se inicioVigencia > fimVigencia ⇒ periodoInvalido=true
 * - Texto: observacao com trim/colapsar espaços (sem destruir abreviações)
 * - Severidade: Fechamento=3, Restrição=2, Modificação=1, outros=0
 * - ativoAgora: nowUTC ∈ [inicio,fim] e statusCodigo ∈ {1,3}
 * - impactoOperacional: Fechamento→NAO_OPERA; Restrição→RESTRITO; Modificação→ALTERADO; senão INFO
 * - Agregação por ICAO: vigentes, vigenteMaisSevero, proximos, historico, estadoAggregado
 */
@ApplicationScoped
public class InfotempParser {

    public InfotempDto parse(String xml) throws Exception {
        Document doc = XmlUtils.createDocument(xml);
        
        NodeList items = doc.getElementsByTagName("item");
        List<InfotempDto.InfotempItemDto> itemList = new ArrayList<>();
        
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            itemList.add(parseItem(item));
        }
        
        // Agregação por ICAO - filtrar items com ICAO válido
        Map<String, List<InfotempDto.InfotempItemDto>> itemsPorIcao = itemList.stream()
            .filter(item -> item.icao() != null && !item.icao().trim().isEmpty())
            .collect(Collectors.groupingBy(InfotempDto.InfotempItemDto::icao));
        
        List<InfotempAgregadoPorIcaoDto> agregados = itemsPorIcao.entrySet().stream()
            .map(entry -> agregarPorIcao(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        return new InfotempDto(itemList.size(), itemList, agregados, null);
    }

    private InfotempDto.InfotempItemDto parseItem(Element item) {
        try {
            String id = getElementText(item, "id");
            String icao = getElementText(item, "AeroCode");
            String observacao = normalizarTexto(getElementText(item, "rmk"));
            
            String inicioVigenciaStr = getElementText(item, "startdate");
            String fimVigenciaStr = getElementText(item, "enddate");
            String dataPublicacaoStr = getElementText(item, "dt");
            
            LocalDateTime inicioVigencia = TsUtils.parseTs(inicioVigenciaStr);
            LocalDateTime fimVigencia = TsUtils.parseTs(fimVigenciaStr);
            LocalDateTime dataPublicacao = TsUtils.parseTs(dataPublicacaoStr);
            
            Integer severidade = calcularSeveridade(observacao);
            Boolean periodoInvalido = inicioVigencia != null && fimVigencia != null && 
                                     inicioVigencia.isAfter(fimVigencia);
            
            String statusCodigo = getElementText(item, "status");
            Boolean ativoAgora = calcularAtivoAgora(inicioVigencia, fimVigencia, statusCodigo);
            String impactoOperacional = calcularImpactoOperacional(severidade);
            
            return new InfotempDto.InfotempItemDto(
                id, icao, observacao, inicioVigencia, fimVigencia, dataPublicacao,
                severidade, ativoAgora, impactoOperacional, periodoInvalido
            );
        } catch (Exception e) {
            // Se houver erro ao parsear um item, criar um item básico com dados mínimos
            return new InfotempDto.InfotempItemDto(
                getElementText(item, "id"),
                getElementText(item, "AeroCode"),
                "Erro ao processar item: " + e.getMessage(),
                null, null, null,
                0, false, "INFO", false
            );
        }
    }

    private InfotempAgregadoPorIcaoDto agregarPorIcao(String icao, List<InfotempDto.InfotempItemDto> items) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        
        List<InfotempDto.InfotempItemDto> vigentes = items.stream()
            .filter(item -> item.ativoAgora())
            .collect(Collectors.toList());
        
        InfotempDto.InfotempItemDto vigenteMaisSevero = vigentes.stream()
            .max(Comparator.comparing(InfotempDto.InfotempItemDto::severidade)
                .thenComparing(InfotempDto.InfotempItemDto::dataPublicacao))
            .orElse(null);
        
        List<InfotempDto.InfotempItemDto> proximos = items.stream()
            .filter(item -> item.inicioVigencia() != null && item.inicioVigencia().isAfter(now))
            .sorted(Comparator.comparing(InfotempDto.InfotempItemDto::inicioVigencia))
            .collect(Collectors.toList());
        
        List<InfotempDto.InfotempItemDto> historico = items.stream()
            .filter(item -> item.fimVigencia() != null && item.fimVigencia().isBefore(now))
            .sorted(Comparator.comparing(InfotempDto.InfotempItemDto::fimVigencia).reversed())
            .collect(Collectors.toList());
        
        String estadoAggregado = calcularEstadoAggregado(vigentes);
        
        return new InfotempAgregadoPorIcaoDto(
            icao, vigentes, vigenteMaisSevero, proximos, historico, estadoAggregado
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        
        // Trim e colapsar espaços múltiplos, mas preservar abreviações
        return texto.trim().replaceAll("\\s+", " ");
    }

    private Integer calcularSeveridade(String observacao) {
        if (observacao == null) {
            return 0;
        }
        
        String obs = observacao.toLowerCase();
        if (obs.contains("fechamento") || obs.contains("fechado")) {
            return 3;
        } else if (obs.contains("restrição") || obs.contains("restringido")) {
            return 2;
        } else if (obs.contains("modificação") || obs.contains("modificado")) {
            return 1;
        }
        return 0;
    }

    private Boolean calcularAtivoAgora(LocalDateTime inicio, LocalDateTime fim, String statusCodigo) {
        if (inicio == null || fim == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        boolean dentroDoPeriodo = !now.isBefore(inicio) && !now.isAfter(fim);
        boolean statusAtivo = "1".equals(statusCodigo) || "3".equals(statusCodigo);
        
        return dentroDoPeriodo && statusAtivo;
    }

    private String calcularImpactoOperacional(Integer severidade) {
        if (severidade == null) {
            return "INFO";
        }
        
        return switch (severidade) {
            case 3 -> "NAO_OPERA";
            case 2 -> "RESTRITO";
            case 1 -> "ALTERADO";
            default -> "INFO";
        };
    }

    private String calcularEstadoAggregado(List<InfotempDto.InfotempItemDto> vigentes) {
        if (vigentes.isEmpty()) {
            return "Sem INFOTEMP vigente";
        }
        
        boolean temFechamento = vigentes.stream()
            .anyMatch(item -> item.severidade() == 3);
        
        if (temFechamento) {
            return "Fechado";
        }
        
        boolean temRestricao = vigentes.stream()
            .anyMatch(item -> item.severidade() == 2);
        
        if (temRestricao) {
            return "Restrito";
        }
        
        boolean temModificacao = vigentes.stream()
            .anyMatch(item -> item.severidade() == 1);
        
        if (temModificacao) {
            return "Modificado";
        }
        
        return "Operacional";
    }
    
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent() != null ? node.getTextContent().trim() : null;
        }
        return null;
    }
}
