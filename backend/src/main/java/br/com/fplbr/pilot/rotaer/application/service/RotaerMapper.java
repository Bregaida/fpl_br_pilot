package br.com.fplbr.pilot.rotaer.application.service;

import br.com.fplbr.pilot.aerodromos.domain.model.Aerodromo;
import br.com.fplbr.pilot.aerodromos.domain.model.Frequencia;
import br.com.fplbr.pilot.aerodromos.domain.model.Pista;
import br.com.fplbr.pilot.rotaer.domain.model.RotaerData;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mapper que converte dados do ROTAER para o modelo de domínio de Aeródromo
 */
@ApplicationScoped
public class RotaerMapper {
    
    private static final Logger log = LoggerFactory.getLogger(RotaerMapper.class);
    
    /**
     * Converte dados do ROTAER para o modelo de domínio Aeródromo
     */
    public Aerodromo toAerodromo(RotaerData rotaerData) {
        if (rotaerData == null || rotaerData.getAerodromo() == null) {
            return null;
        }
        
        RotaerData.AerodromoInfo aerodromoInfo = rotaerData.getAerodromo();
        
        // Mapear informações básicas
        String nome = aerodromoInfo.getNome();
        if (nome == null || nome.trim().isEmpty()) {
            nome = aerodromoInfo.getIcao(); // Fallback para ICAO
            log.warn("Nome do aeródromo {} estava vazio, usando ICAO como fallback", aerodromoInfo.getIcao());
        }
        
        Aerodromo.AerodromoBuilder builder = Aerodromo.builder()
            .icao(aerodromoInfo.getIcao())
            .nome(nome)
            .municipio(aerodromoInfo.getMunicipio())
            .uf(aerodromoInfo.getUf())
            .tipo(aerodromoInfo.getTipo())
            .uso(aerodromoInfo.getUtilizacao())
            .responsavel(aerodromoInfo.getAdministrador())
            .observacoes(aerodromoInfo.getObservacoesGerais())
            .ativo(true);
        
        // Mapear coordenadas
        if (aerodromoInfo.getCoordsArp() != null) {
            builder.latitude(aerodromoInfo.getCoordsArp().getLatDd())
                   .longitude(aerodromoInfo.getCoordsArp().getLonDd());
        }
        
        // Mapear elevação
        if (aerodromoInfo.getElevacaoFt() != null) {
            builder.altitudePes(aerodromoInfo.getElevacaoFt());
        }
        
        // Mapear categoria para internacional
        if (aerodromoInfo.getCategoria() != null) {
            boolean internacional = aerodromoInfo.getCategoria().contains("INTL");
            builder.internacional(internacional);
        }
        
        // Mapear operação para terminal
        if (aerodromoInfo.getOperacao() != null) {
            boolean terminal = aerodromoInfo.getOperacao().contains("IFR");
            builder.terminal(terminal);
        }
        
        // Mapear horário de funcionamento
        if (aerodromoInfo.getOperacao() != null) {
            builder.horarioFuncionamento(aerodromoInfo.getOperacao());
        }
        
        // Mapear pistas
        List<Pista> pistas = mapPistas(rotaerData.getPistas());
        builder.pistas(pistas);
        
        // Mapear frequências
        List<Frequencia> frequencias = mapFrequencias(rotaerData.getComunicacoes());
        builder.frequencias(frequencias);
        
        // Mapear informações adicionais do ROTAER para observações
        String observacoesCompletas = buildObservacoesCompletas(rotaerData);
        builder.observacoes(observacoesCompletas);
        
        Aerodromo aerodromo = builder.build();
        
        log.debug("Mapeado aeródromo {} com {} pistas e {} frequências", 
                 aerodromo.getIcao(), pistas.size(), frequencias.size());
        
        return aerodromo;
    }
    
    /**
     * Mapeia pistas do ROTAER para o modelo de domínio
     */
    private List<Pista> mapPistas(List<RotaerData.PistaInfo> pistasRotaer) {
        List<Pista> pistas = new ArrayList<>();
        
        if (pistasRotaer == null) {
            return pistas;
        }
        
        for (RotaerData.PistaInfo pistaRotaer : pistasRotaer) {
            Pista.PistaBuilder builder = Pista.builder();
            
            // Designadores
            if (pistaRotaer.getDesignadores() != null && !pistaRotaer.getDesignadores().isEmpty()) {
                String designadores = String.join("/", pistaRotaer.getDesignadores());
                builder.designacao(designadores);
            }
            
            // Dimensões
            if (pistaRotaer.getDimensoes() != null) {
                builder.comprimentoMetros(pistaRotaer.getDimensoes().getComprimentoM().doubleValue());
                builder.larguraMetros(pistaRotaer.getDimensoes().getLarguraM().doubleValue());
            }
            
            // Piso
            builder.superficie(pistaRotaer.getPiso());
            
            // PCN
            if (pistaRotaer.getPcn() != null) {
                // Extrair número PCN do formato "73/F/B/X/T"
                String[] pcnParts = pistaRotaer.getPcn().split("/");
                if (pcnParts.length > 0) {
                    try {
                        builder.resistenciaPcn(Integer.parseInt(pcnParts[0]));
                    } catch (NumberFormatException e) {
                        // Ignorar se não conseguir converter
                    }
                }
                builder.classificacaoPcn(pistaRotaer.getPcn());
            }
            
            // Luzes
            if (pistaRotaer.getLuzes() != null && !pistaRotaer.getLuzes().isEmpty()) {
                String luzes = String.join(", ", pistaRotaer.getLuzes());
                builder.luzesBorda(luzes);
            }
            
            // MEHT
            if (pistaRotaer.getMeht() != null) {
                builder.observacoes("MEHT: " + pistaRotaer.getMeht().getValorFt() + " FT");
            }
            
            // Observações
            if (pistaRotaer.getObservacoes() != null) {
                String obs = pistaRotaer.getObservacoes();
                if (builder.build().getObservacoes() != null) {
                    obs = builder.build().getObservacoes() + " | " + obs;
                }
                builder.observacoes(obs);
            }
            
            pistas.add(builder.build());
        }
        
        return pistas;
    }
    
    /**
     * Mapeia comunicações do ROTAER para frequências do modelo de domínio
     */
    private List<Frequencia> mapFrequencias(List<RotaerData.ComunicacaoInfo> comunicacoes) {
        List<Frequencia> frequencias = new ArrayList<>();
        
        if (comunicacoes == null) {
            return frequencias;
        }
        
        for (RotaerData.ComunicacaoInfo comunicacao : comunicacoes) {
            if (comunicacao.getFrequenciasMhz() == null) {
                continue;
            }
            
            for (Double freq : comunicacao.getFrequenciasMhz()) {
                Frequencia.FrequenciaBuilder builder = Frequencia.builder();
                
                // Mapear tipo
                Frequencia.TipoFrequencia tipo = mapTipoFrequencia(comunicacao.getOrgaos());
                builder.tipo(tipo);
                
                // Descrição
                builder.descricao(comunicacao.getIndicativo());
                
                // Valor da frequência
                builder.valor(freq.toString());
                
                // Horário de funcionamento
                builder.horarioFuncionamento(comunicacao.getHorarioOperacao());
                
                // Observações
                StringBuilder obs = new StringBuilder();
                if (comunicacao.getEmergencia() != null && comunicacao.getEmergencia()) {
                    obs.append("EMERGÊNCIA ");
                }
                if (comunicacao.getTipo() != null) {
                    obs.append("TIPO: ").append(comunicacao.getTipo());
                }
                builder.observacoes(obs.toString().trim());
                
                frequencias.add(builder.build());
            }
        }
        
        return frequencias;
    }
    
    /**
     * Mapeia órgão para tipo de frequência
     */
    private Frequencia.TipoFrequencia mapTipoFrequencia(String orgaos) {
        if (orgaos == null) {
            return Frequencia.TipoFrequencia.RADIO;
        }
        
        String upper = orgaos.toUpperCase();
        if (upper.contains("TWR")) {
            return Frequencia.TipoFrequencia.TWR;
        } else if (upper.contains("GND")) {
            return Frequencia.TipoFrequencia.GND;
        } else if (upper.contains("ATIS")) {
            return Frequencia.TipoFrequencia.ATIS;
        } else if (upper.contains("APP")) {
            return Frequencia.TipoFrequencia.APCH;
        } else if (upper.contains("DEP")) {
            return Frequencia.TipoFrequencia.DEP;
        } else {
            return Frequencia.TipoFrequencia.RADIO;
        }
    }
    
    /**
     * Constrói observações completas incluindo informações do ROTAER
     */
    private String buildObservacoesCompletas(RotaerData rotaerData) {
        StringBuilder observacoes = new StringBuilder();
        
        RotaerData.AerodromoInfo aerodromo = rotaerData.getAerodromo();
        
        // Observações básicas do aeródromo
        if (aerodromo.getObservacoesGerais() != null && !aerodromo.getObservacoesGerais().trim().isEmpty()) {
            observacoes.append("OBSERVAÇÕES: ").append(aerodromo.getObservacoesGerais()).append("\n");
        }
        
        // FIR e Jurisdição
        if (aerodromo.getFir() != null && !aerodromo.getFir().trim().isEmpty()) {
            observacoes.append("FIR: ").append(aerodromo.getFir()).append("\n");
        }
        if (aerodromo.getJurisdicao() != null && !aerodromo.getJurisdicao().trim().isEmpty()) {
            observacoes.append("JURISDIÇÃO: ").append(aerodromo.getJurisdicao()).append("\n");
        }
        
        // Luzes do aeródromo
        if (aerodromo.getLuzesAerodromo() != null && !aerodromo.getLuzesAerodromo().isEmpty()) {
            observacoes.append("LUZES AERÓDROMO: ").append(String.join(", ", aerodromo.getLuzesAerodromo())).append("\n");
        }
        
        // Fuso horário
        if (aerodromo.getFuso() != null && !aerodromo.getFuso().trim().isEmpty()) {
            observacoes.append("FUSO: ").append(aerodromo.getFuso()).append("\n");
        }
        
        // Distância da cidade
        if (aerodromo.getDistanciaDirecaoCidade() != null) {
            RotaerData.DistanciaDirecao distDir = aerodromo.getDistanciaDirecaoCidade();
            observacoes.append("DISTÂNCIA DA CIDADE: ").append(distDir.getKm()).append(" KM ").append(distDir.getDirecao()).append("\n");
        }
        
        // Serviços
        if (rotaerData.getServicos() != null) {
            RotaerData.ServicosInfo servicos = rotaerData.getServicos();
            
            // Combustível
            if (servicos.getCombustivel() != null && !servicos.getCombustivel().isEmpty()) {
                observacoes.append("COMBUSTÍVEL: ").append(String.join(", ", servicos.getCombustivel())).append("\n");
            }
            
            // Manutenção
            if (servicos.getManutencao() != null && !servicos.getManutencao().isEmpty()) {
                observacoes.append("MANUTENÇÃO: ").append(String.join(", ", servicos.getManutencao())).append("\n");
            }
            
            // RFFS
            if (servicos.getRffs() != null) {
                RotaerData.RffsInfo rffs = servicos.getRffs();
                observacoes.append("RFFS - CAT CIVIL: ").append(rffs.getCatCivil()).append(", CAT MIL: ").append(rffs.getCatMil()).append("\n");
            }
            
            // MET
            if (servicos.getMet() != null) {
                RotaerData.MetInfo met = servicos.getMet();
                if (met.getTelefones() != null && !met.getTelefones().isEmpty()) {
                    observacoes.append("MET TEL: ").append(String.join(", ", met.getTelefones())).append("\n");
                }
            }
            
            // AIS
            if (servicos.getAis() != null) {
                RotaerData.AisInfo ais = servicos.getAis();
                if (ais.getTelefones() != null && !ais.getTelefones().isEmpty()) {
                    observacoes.append("AIS TEL: ").append(String.join(", ", ais.getTelefones())).append("\n");
                }
            }
            
            // RMK
            if (servicos.getRmk() != null && !servicos.getRmk().trim().isEmpty()) {
                observacoes.append("RMK: ").append(servicos.getRmk()).append("\n");
            }
        }
        
        // RDONAV
        if (rotaerData.getRdonav() != null && !rotaerData.getRdonav().isEmpty()) {
            observacoes.append("RDONAV:\n");
            for (RotaerData.RdonavInfo nav : rotaerData.getRdonav()) {
                observacoes.append("  ").append(nav.getTipo()).append(" ").append(nav.getIdent())
                          .append(" ").append(nav.getFreq()).append(" MHz");
                if (nav.getPistaAssociada() != null) {
                    observacoes.append(" (RWY ").append(nav.getPistaAssociada()).append(")");
                }
                observacoes.append("\n");
            }
        }
        
        // INFOTEMP
        if (rotaerData.getInfotemp() != null && !rotaerData.getInfotemp().isEmpty()) {
            observacoes.append("INFOTEMP:\n");
            for (RotaerData.InfotempInfo info : rotaerData.getInfotemp()) {
                observacoes.append("  ").append(info.getId()).append(" - ").append(info.getNatureza())
                          .append(": ").append(info.getTexto()).append("\n");
            }
        }
        
        // Fonte
        if (rotaerData.getFonte() != null) {
            observacoes.append("FONTE: ").append(rotaerData.getFonte().getDocumento())
                      .append(" ").append(rotaerData.getFonte().getVersao())
                      .append(" capturado em ").append(rotaerData.getFonte().getCapturadoEm()).append("\n");
        }
        
        return observacoes.toString().trim();
    }
    
    /**
     * Cria um diff entre o aeródromo existente e os novos dados do ROTAER
     */
    public String createDiff(Aerodromo existing, Aerodromo updated) {
        StringBuilder diff = new StringBuilder();
        
        if (existing == null) {
            diff.append("NOVO AERÓDROMO: ").append(updated.getIcao()).append("\n");
            return diff.toString();
        }
        
        // Comparar campos básicos
        if (!Objects.equals(existing.getNome(), updated.getNome())) {
            diff.append("NOME: ").append(existing.getNome()).append(" -> ").append(updated.getNome()).append("\n");
        }
        
        if (!Objects.equals(existing.getMunicipio(), updated.getMunicipio())) {
            diff.append("MUNICÍPIO: ").append(existing.getMunicipio()).append(" -> ").append(updated.getMunicipio()).append("\n");
        }
        
        if (!Objects.equals(existing.getUf(), updated.getUf())) {
            diff.append("UF: ").append(existing.getUf()).append(" -> ").append(updated.getUf()).append("\n");
        }
        
        if (!Objects.equals(existing.getLatitude(), updated.getLatitude())) {
            diff.append("LATITUDE: ").append(existing.getLatitude()).append(" -> ").append(updated.getLatitude()).append("\n");
        }
        
        if (!Objects.equals(existing.getLongitude(), updated.getLongitude())) {
            diff.append("LONGITUDE: ").append(existing.getLongitude()).append(" -> ").append(updated.getLongitude()).append("\n");
        }
        
        if (!Objects.equals(existing.getAltitudePes(), updated.getAltitudePes())) {
            diff.append("ALTITUDE: ").append(existing.getAltitudePes()).append(" -> ").append(updated.getAltitudePes()).append("\n");
        }
        
        if (!Objects.equals(existing.getTipo(), updated.getTipo())) {
            diff.append("TIPO: ").append(existing.getTipo()).append(" -> ").append(updated.getTipo()).append("\n");
        }
        
        if (!Objects.equals(existing.getUso(), updated.getUso())) {
            diff.append("USO: ").append(existing.getUso()).append(" -> ").append(updated.getUso()).append("\n");
        }
        
        if (!Objects.equals(existing.getInternacional(), updated.getInternacional())) {
            diff.append("INTERNACIONAL: ").append(existing.getInternacional()).append(" -> ").append(updated.getInternacional()).append("\n");
        }
        
        if (!Objects.equals(existing.getTerminal(), updated.getTerminal())) {
            diff.append("TERMINAL: ").append(existing.getTerminal()).append(" -> ").append(updated.getTerminal()).append("\n");
        }
        
        // Comparar pistas
        diff.append(comparePistas(existing.getPistas(), updated.getPistas()));
        
        // Comparar frequências
        diff.append(compareFrequencias(existing.getFrequencias(), updated.getFrequencias()));
        
        return diff.toString().trim();
    }
    
    /**
     * Compara listas de pistas
     */
    private String comparePistas(List<Pista> existing, List<Pista> updated) {
        StringBuilder diff = new StringBuilder();
        
        if (existing.size() != updated.size()) {
            diff.append("PISTAS: ").append(existing.size()).append(" -> ").append(updated.size()).append("\n");
        }
        
        // Comparar pistas individuais (simplificado)
        for (int i = 0; i < Math.min(existing.size(), updated.size()); i++) {
            Pista existingPista = existing.get(i);
            Pista updatedPista = updated.get(i);
            
            if (!Objects.equals(existingPista.getDesignacao(), updatedPista.getDesignacao())) {
                diff.append("PISTA ").append(i + 1).append(" DESIGNAÇÃO: ")
                    .append(existingPista.getDesignacao()).append(" -> ").append(updatedPista.getDesignacao()).append("\n");
            }
            
            if (!Objects.equals(existingPista.getComprimentoMetros(), updatedPista.getComprimentoMetros())) {
                diff.append("PISTA ").append(i + 1).append(" COMPRIMENTO: ")
                    .append(existingPista.getComprimentoMetros()).append(" -> ").append(updatedPista.getComprimentoMetros()).append("\n");
            }
        }
        
        return diff.toString();
    }
    
    /**
     * Compara listas de frequências
     */
    private String compareFrequencias(List<Frequencia> existing, List<Frequencia> updated) {
        StringBuilder diff = new StringBuilder();
        
        if (existing.size() != updated.size()) {
            diff.append("FREQUÊNCIAS: ").append(existing.size()).append(" -> ").append(updated.size()).append("\n");
        }
        
        return diff.toString();
    }
}
