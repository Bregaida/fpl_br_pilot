package br.com.fplbr.pilot.rotaer.application.service;

import br.com.fplbr.pilot.rotaer.domain.model.RotaerData;
import br.com.fplbr.pilot.rotaer.domain.model.ValidationWarning;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador de dados do ROTAER
 * Aplica regras de validação e gera warnings
 */
@ApplicationScoped
public class RotaerValidator {
    
    private static final Logger log = LoggerFactory.getLogger(RotaerValidator.class);
    
    // Padrões de validação
    private static final Pattern ICAO_PATTERN = Pattern.compile("^[A-Z]{4}$");
    private static final Pattern UF_PATTERN = Pattern.compile("^[A-Z]{2}$");
    private static final Pattern RUNWAY_DESIGNATOR_PATTERN = Pattern.compile("^(0[1-9]|[12][0-9]|3[0-6])$");
    private static final Pattern PCN_PATTERN = Pattern.compile("^[0-9]+/([RF])/([ABCD])/([WXYZ])/([TU])$");
    private static final Pattern L_CODE_PATTERN = Pattern.compile("^L([1-9]|[12][0-9]|3[0-5])([A-Z])?(\\([0-9.]+\\))?$");
    private static final Pattern INFOTEMP_ID_PATTERN = Pattern.compile("^[FRCM][0-9]{4}/[0-9]{4}$");
    
    // Limites configuráveis
    private static final double MIN_LAT_BRASIL = -34.0;
    private static final double MAX_LAT_BRASIL = -5.0;
    private static final double MIN_LON_BRASIL = -74.0;
    private static final double MAX_LON_BRASIL = -34.0;
    
    private static final int MIN_RUNWAY_LENGTH = 400;
    private static final int MAX_RUNWAY_LENGTH = 5000;
    private static final int MIN_RUNWAY_WIDTH = 10;
    private static final int MAX_RUNWAY_WIDTH = 80;
    
    private static final double MIN_FREQUENCY = 108.000;
    private static final double MAX_FREQUENCY = 136.975;
    private static final double EMERGENCY_FREQUENCY = 121.500;
    private static final double NDB_FREQUENCY_MIN = 200.0;
    private static final double NDB_FREQUENCY_MAX = 1750.0;
    
    private static final int MIN_RFFS_CATEGORY = 1;
    private static final int MAX_RFFS_CATEGORY = 10;
    
    /**
     * Valida dados completos do ROTAER
     */
    public List<ValidationWarning> validate(RotaerData data) {
        List<ValidationWarning> warnings = new ArrayList<>();
        
        if (data == null) {
            warnings.add(new ValidationWarning("rotaer_data", "null", "dados_obrigatorios", 
                                             ValidationWarning.Severity.ERROR, "Dados do ROTAER são obrigatórios"));
            return warnings;
        }
        
        validateAerodromo(data.getAerodromo(), warnings);
        validatePistas(data.getPistas(), warnings);
        validateComunicacoes(data.getComunicacoes(), warnings);
        validateRdonav(data.getRdonav(), warnings);
        validateServicos(data.getServicos(), warnings);
        validateInfotemp(data.getInfotemp(), warnings);
        
        return warnings;
    }
    
    /**
     * Valida informações do aeródromo
     */
    private void validateAerodromo(RotaerData.AerodromoInfo aerodromo, List<ValidationWarning> warnings) {
        if (aerodromo == null) {
            warnings.add(new ValidationWarning("aerodromo", "null", "aerodromo_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "Informações do aeródromo são obrigatórias"));
            return;
        }
        
        validateIcao(aerodromo.getIcao(), warnings);
        validateUf(aerodromo.getUf(), aerodromo.getIcao(), warnings);
        validateCoordenadas(aerodromo.getCoordsArp(), warnings);
        validateElevacao(aerodromo.getElevacaoM(), aerodromo.getElevacaoFt(), warnings);
        validateTipo(aerodromo.getTipo(), warnings);
        validateCategoria(aerodromo.getCategoria(), warnings);
        validateUtilizacao(aerodromo.getUtilizacao(), warnings);
        validateOperacao(aerodromo.getOperacao(), warnings);
        validateLuzes(aerodromo.getLuzesAerodromo(), warnings);
    }
    
    /**
     * Valida código ICAO
     */
    private void validateIcao(String icao, List<ValidationWarning> warnings) {
        if (icao == null || icao.trim().isEmpty()) {
            warnings.add(new ValidationWarning("icao", icao, "icao_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "Código ICAO é obrigatório"));
            return;
        }
        
        if (!ICAO_PATTERN.matcher(icao.trim().toUpperCase()).matches()) {
            warnings.add(new ValidationWarning("icao", icao, "formato_icao_invalido", 
                                             ValidationWarning.Severity.ERROR, "Código ICAO deve ter 4 letras maiúsculas"));
        }
    }
    
    /**
     * Valida UF
     */
    private void validateUf(String uf, String icao, List<ValidationWarning> warnings) {
        // UF é obrigatória apenas para códigos brasileiros (que começam com S)
        boolean isBrasileiro = icao != null && icao.startsWith("S");
        
        if (uf == null || uf.trim().isEmpty()) {
            if (isBrasileiro) {
                warnings.add(new ValidationWarning("uf", uf, "uf_obrigatorio", 
                                                 ValidationWarning.Severity.ERROR, "UF é obrigatória para aeródromos brasileiros"));
            } else {
                warnings.add(new ValidationWarning("uf", uf, "uf_opcional", 
                                                 ValidationWarning.Severity.INFO, "UF é opcional para aeródromos não-brasileiros"));
            }
            return;
        }
        
        if (!UF_PATTERN.matcher(uf.trim().toUpperCase()).matches()) {
            warnings.add(new ValidationWarning("uf", uf, "formato_uf_invalido", 
                                             ValidationWarning.Severity.ERROR, "UF deve ter 2 letras maiúsculas"));
        }
    }
    
    /**
     * Valida coordenadas
     */
    private void validateCoordenadas(RotaerData.Coordenadas coords, List<ValidationWarning> warnings) {
        if (coords == null) {
            warnings.add(new ValidationWarning("coordenadas", "null", "coordenadas_obrigatorias", 
                                             ValidationWarning.Severity.WARNING, "Coordenadas são recomendadas"));
            return;
        }
        
        Double lat = coords.getLatDd();
        Double lon = coords.getLonDd();
        
        if (lat == null || lon == null) {
            warnings.add(new ValidationWarning("coordenadas", "lat/lon null", "coordenadas_decimais_obrigatorias", 
                                             ValidationWarning.Severity.ERROR, "Coordenadas decimais são obrigatórias"));
            return;
        }
        
        // Validar se está dentro do Brasil
        if (lat < MIN_LAT_BRASIL || lat > MAX_LAT_BRASIL) {
            warnings.add(new ValidationWarning("latitude", lat.toString(), "fora_limites_brasil", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Latitude fora dos limites do Brasil (%.1f a %.1f)", MIN_LAT_BRASIL, MAX_LAT_BRASIL)));
        }
        
        if (lon < MIN_LON_BRASIL || lon > MAX_LON_BRASIL) {
            warnings.add(new ValidationWarning("longitude", lon.toString(), "fora_limites_brasil", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Longitude fora dos limites do Brasil (%.1f a %.1f)", MIN_LON_BRASIL, MAX_LON_BRASIL)));
        }
    }
    
    /**
     * Valida elevação
     */
    private void validateElevacao(Integer elevacaoM, Integer elevacaoFt, List<ValidationWarning> warnings) {
        if (elevacaoM == null && elevacaoFt == null) {
            warnings.add(new ValidationWarning("elevacao", "null", "elevacao_obrigatoria", 
                                             ValidationWarning.Severity.WARNING, "Elevação é recomendada"));
            return;
        }
        
        // Validar conversão entre metros e pés
        if (elevacaoM != null && elevacaoFt != null) {
            double expectedFt = elevacaoM * 3.28084;
            double diff = Math.abs(elevacaoFt - expectedFt);
            if (diff > 1.0) { // Tolerância de 1 pé
                warnings.add(new ValidationWarning("elevacao", elevacaoM + "m/" + elevacaoFt + "ft", "conversao_inconsistente", 
                                                 ValidationWarning.Severity.WARNING, 
                                                 String.format("Conversão inconsistente: %dm = %.0fft (esperado %dft)", 
                                                             elevacaoM, expectedFt, elevacaoFt)));
            }
        }
    }
    
    /**
     * Valida tipo de aeródromo
     */
    private void validateTipo(String tipo, List<ValidationWarning> warnings) {
        if (tipo == null || tipo.trim().isEmpty()) {
            warnings.add(new ValidationWarning("tipo", tipo, "tipo_obrigatorio", 
                                             ValidationWarning.Severity.WARNING, "Tipo de aeródromo é recomendado"));
            return;
        }
        
        String normalized = tipo.trim().toUpperCase();
        if (!normalized.matches("^(AD|HELPN|HD)$")) {
            warnings.add(new ValidationWarning("tipo", tipo, "tipo_nao_reconhecido", 
                                             ValidationWarning.Severity.INFO, "Tipo não reconhecido: " + tipo));
        }
    }
    
    /**
     * Valida categoria
     */
    private void validateCategoria(String categoria, List<ValidationWarning> warnings) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return; // Categoria é opcional
        }
        
        String normalized = categoria.trim().toUpperCase();
        if (!normalized.matches("^(INTL|INTL/ALTN|NATL|REG|LOC|PRIV|MIL|MIL/ALTN)$")) {
            warnings.add(new ValidationWarning("categoria", categoria, "categoria_nao_reconhecida", 
                                             ValidationWarning.Severity.INFO, "Categoria não reconhecida: " + categoria));
        }
    }
    
    /**
     * Valida utilização
     */
    private void validateUtilizacao(String utilizacao, List<ValidationWarning> warnings) {
        if (utilizacao == null || utilizacao.trim().isEmpty()) {
            return; // Utilização é opcional
        }
        
        String normalized = utilizacao.trim().toUpperCase();
        if (!normalized.matches("^(PUB|PRIV|PUB/MIL|PUB/REST|PRIV/PUB|MIL)$")) {
            warnings.add(new ValidationWarning("utilizacao", utilizacao, "utilizacao_nao_reconhecida", 
                                             ValidationWarning.Severity.INFO, "Utilização não reconhecida: " + utilizacao));
        }
    }
    
    /**
     * Valida operação
     */
    private void validateOperacao(String operacao, List<ValidationWarning> warnings) {
        if (operacao == null || operacao.trim().isEmpty()) {
            return; // Operação é opcional
        }
        
        String normalized = operacao.trim().toUpperCase();
        if (!normalized.matches("^(VFR IFR|IFR|IFR DIURNA|VFR IFR DIURNA|VFR|VFR DIURNA)$")) {
            warnings.add(new ValidationWarning("operacao", operacao, "operacao_nao_reconhecida", 
                                             ValidationWarning.Severity.INFO, "Operação não reconhecida: " + operacao));
        }
    }
    
    /**
     * Valida luzes
     */
    private void validateLuzes(List<String> luzes, List<ValidationWarning> warnings) {
        if (luzes == null || luzes.isEmpty()) {
            return; // Luzes são opcionais
        }
        
        for (String luz : luzes) {
            if (!L_CODE_PATTERN.matcher(luz.trim().toUpperCase()).matches()) {
                warnings.add(new ValidationWarning("luzes", luz, "l_code_invalido", 
                                                 ValidationWarning.Severity.WARNING, "L-code inválido: " + luz));
            }
        }
    }
    
    /**
     * Valida pistas
     */
    private void validatePistas(List<RotaerData.PistaInfo> pistas, List<ValidationWarning> warnings) {
        if (pistas == null || pistas.isEmpty()) {
            warnings.add(new ValidationWarning("pistas", "empty", "pistas_obrigatorias", 
                                             ValidationWarning.Severity.WARNING, "Pelo menos uma pista é recomendada"));
            return;
        }
        
        for (int i = 0; i < pistas.size(); i++) {
            RotaerData.PistaInfo pista = pistas.get(i);
            String prefix = "pistas[" + i + "]";
            
            validateDesignadores(prefix, pista.getDesignadores(), warnings);
            validateDimensoes(prefix, pista.getDimensoes(), warnings);
            validatePcn(prefix, pista.getPcn(), warnings);
            validateLuzesPista(prefix, pista.getLuzes(), warnings);
        }
    }
    
    /**
     * Valida designadores de pista
     */
    private void validateDesignadores(String prefix, List<String> designadores, List<ValidationWarning> warnings) {
        if (designadores == null || designadores.isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".designadores", "empty", "designadores_obrigatorios", 
                                             ValidationWarning.Severity.ERROR, "Designadores de pista são obrigatórios"));
            return;
        }
        
        for (String designador : designadores) {
            if (!RUNWAY_DESIGNATOR_PATTERN.matcher(designador.trim()).matches()) {
                warnings.add(new ValidationWarning(prefix + ".designadores", designador, "designador_invalido", 
                                                 ValidationWarning.Severity.ERROR, "Designador de pista inválido: " + designador));
            }
        }
    }
    
    /**
     * Valida dimensões de pista
     */
    private void validateDimensoes(String prefix, RotaerData.DimensoesPista dimensoes, List<ValidationWarning> warnings) {
        if (dimensoes == null) {
            warnings.add(new ValidationWarning(prefix + ".dimensoes", "null", "dimensoes_obrigatorias", 
                                             ValidationWarning.Severity.ERROR, "Dimensões da pista são obrigatórias"));
            return;
        }
        
        Integer comprimento = dimensoes.getComprimentoM();
        Integer largura = dimensoes.getLarguraM();
        
        if (comprimento == null) {
            warnings.add(new ValidationWarning(prefix + ".comprimento", "null", "comprimento_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "Comprimento da pista é obrigatório"));
        } else if (comprimento < MIN_RUNWAY_LENGTH || comprimento > MAX_RUNWAY_LENGTH) {
            warnings.add(new ValidationWarning(prefix + ".comprimento", comprimento.toString(), "comprimento_fora_limites", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Comprimento fora dos limites típicos (%d a %dm)", MIN_RUNWAY_LENGTH, MAX_RUNWAY_LENGTH)));
        }
        
        if (largura == null) {
            warnings.add(new ValidationWarning(prefix + ".largura", "null", "largura_obrigatoria", 
                                             ValidationWarning.Severity.ERROR, "Largura da pista é obrigatória"));
        } else if (largura < MIN_RUNWAY_WIDTH || largura > MAX_RUNWAY_WIDTH) {
            warnings.add(new ValidationWarning(prefix + ".largura", largura.toString(), "largura_fora_limites", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Largura fora dos limites típicos (%d a %dm)", MIN_RUNWAY_WIDTH, MAX_RUNWAY_WIDTH)));
        }
    }
    
    /**
     * Valida PCN
     */
    private void validatePcn(String prefix, String pcn, List<ValidationWarning> warnings) {
        if (pcn == null || pcn.trim().isEmpty()) {
            return; // PCN é opcional
        }
        
        if (!PCN_PATTERN.matcher(pcn.trim().toUpperCase()).matches()) {
            warnings.add(new ValidationWarning(prefix + ".pcn", pcn, "pcn_formato_invalido", 
                                             ValidationWarning.Severity.WARNING, "Formato PCN inválido: " + pcn));
        }
    }
    
    /**
     * Valida luzes de pista
     */
    private void validateLuzesPista(String prefix, List<String> luzes, List<ValidationWarning> warnings) {
        if (luzes == null || luzes.isEmpty()) {
            return; // Luzes são opcionais
        }
        
        for (String luz : luzes) {
            if (!L_CODE_PATTERN.matcher(luz.trim().toUpperCase()).matches()) {
                warnings.add(new ValidationWarning(prefix + ".luzes", luz, "l_code_invalido", 
                                                 ValidationWarning.Severity.WARNING, "L-code inválido: " + luz));
            }
        }
    }
    
    /**
     * Valida comunicações
     */
    private void validateComunicacoes(List<RotaerData.ComunicacaoInfo> comunicacoes, List<ValidationWarning> warnings) {
        if (comunicacoes == null || comunicacoes.isEmpty()) {
            return; // Comunicações são opcionais
        }
        
        for (int i = 0; i < comunicacoes.size(); i++) {
            RotaerData.ComunicacaoInfo comm = comunicacoes.get(i);
            String prefix = "comunicacoes[" + i + "]";
            
            validateFrequencias(prefix, comm.getFrequenciasMhz(), warnings);
            validateOrgaos(prefix, comm.getOrgaos(), warnings);
        }
    }
    
    /**
     * Valida frequências
     */
    private void validateFrequencias(String prefix, List<Double> frequencias, List<ValidationWarning> warnings) {
        if (frequencias == null || frequencias.isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".frequencias", "empty", "frequencias_obrigatorias", 
                                             ValidationWarning.Severity.ERROR, "Frequências são obrigatórias"));
            return;
        }
        
        for (Double freq : frequencias) {
            if (freq == null) continue;
            
            // Validar faixa VHF
            if (freq >= MIN_FREQUENCY && freq <= MAX_FREQUENCY) {
                // Frequência VHF válida
                if (freq.equals(EMERGENCY_FREQUENCY)) {
                    // Marcar como emergência se não estiver marcado
                    log.debug("Frequência de emergência detectada: {}", freq);
                }
            } else if (freq >= NDB_FREQUENCY_MIN && freq <= NDB_FREQUENCY_MAX) {
                // Frequência NDB válida
                log.debug("Frequência NDB detectada: {}", freq);
            } else {
                warnings.add(new ValidationWarning(prefix + ".frequencias", freq.toString(), "frequencia_fora_faixa", 
                                                 ValidationWarning.Severity.WARNING, 
                                                 String.format("Frequência fora das faixas válidas (VHF: %.3f-%.3f, NDB: %.0f-%.0f)", 
                                                             MIN_FREQUENCY, MAX_FREQUENCY, NDB_FREQUENCY_MIN, NDB_FREQUENCY_MAX)));
            }
        }
    }
    
    /**
     * Valida órgãos de comunicação
     */
    private void validateOrgaos(String prefix, String orgaos, List<ValidationWarning> warnings) {
        if (orgaos == null || orgaos.trim().isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".orgaos", "empty", "orgaos_obrigatorios", 
                                             ValidationWarning.Severity.ERROR, "Órgãos são obrigatórios"));
            return;
        }
        
        String normalized = orgaos.trim().toUpperCase();
        if (!normalized.matches("^(TWR|GND|TRÁFEGO|ATIS|APP|ACC|VOLMET|FIS|RDG)(\\s*,\\s*(TWR|GND|TRÁFEGO|ATIS|APP|ACC|VOLMET|FIS|RDG))*$")) {
            warnings.add(new ValidationWarning(prefix + ".orgaos", orgaos, "orgaos_nao_reconhecidos", 
                                             ValidationWarning.Severity.INFO, "Órgãos não reconhecidos: " + orgaos));
        }
    }
    
    /**
     * Valida RDONAV
     */
    private void validateRdonav(List<RotaerData.RdonavInfo> rdonav, List<ValidationWarning> warnings) {
        if (rdonav == null || rdonav.isEmpty()) {
            return; // RDONAV é opcional
        }
        
        for (int i = 0; i < rdonav.size(); i++) {
            RotaerData.RdonavInfo nav = rdonav.get(i);
            String prefix = "rdonav[" + i + "]";
            
            validateTipoRdonav(prefix, nav.getTipo(), warnings);
            validateFrequenciaRdonav(prefix, nav.getFreq(), warnings);
            validateCoordenadas(prefix, nav.getCoords(), warnings);
        }
    }
    
    /**
     * Valida tipo de RDONAV
     */
    private void validateTipoRdonav(String prefix, String tipo, List<ValidationWarning> warnings) {
        if (tipo == null || tipo.trim().isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".tipo", "empty", "tipo_rdonav_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "Tipo de RDONAV é obrigatório"));
            return;
        }
        
        String normalized = tipo.trim().toUpperCase();
        if (!normalized.matches("^(ILS/DME|VOR/DME|NDB|OM|IM)$")) {
            warnings.add(new ValidationWarning(prefix + ".tipo", tipo, "tipo_rdonav_nao_reconhecido", 
                                             ValidationWarning.Severity.INFO, "Tipo de RDONAV não reconhecido: " + tipo));
        }
    }
    
    /**
     * Valida frequência de RDONAV
     */
    private void validateFrequenciaRdonav(String prefix, Double freq, List<ValidationWarning> warnings) {
        if (freq == null) {
            warnings.add(new ValidationWarning(prefix + ".freq", "null", "frequencia_rdonav_obrigatoria", 
                                             ValidationWarning.Severity.ERROR, "Frequência de RDONAV é obrigatória"));
            return;
        }
        
        // Validar faixas específicas para RDONAV
        if (freq >= 108.0 && freq <= 118.0) {
            // ILS/VOR válido
        } else if (freq >= 200.0 && freq <= 1750.0) {
            // NDB válido
        } else {
            warnings.add(new ValidationWarning(prefix + ".freq", freq.toString(), "frequencia_rdonav_fora_faixa", 
                                             ValidationWarning.Severity.WARNING, 
                                             "Frequência de RDONAV fora das faixas válidas (ILS/VOR: 108-118, NDB: 200-1750)"));
        }
    }
    
    /**
     * Valida coordenadas de RDONAV
     */
    private void validateCoordenadas(String prefix, RotaerData.Coordenadas coords, List<ValidationWarning> warnings) {
        if (coords == null) {
            return; // Coordenadas de RDONAV são opcionais
        }
        
        validateCoordenadas(coords, warnings);
    }
    
    /**
     * Valida serviços
     */
    private void validateServicos(RotaerData.ServicosInfo servicos, List<ValidationWarning> warnings) {
        if (servicos == null) {
            return; // Serviços são opcionais
        }
        
        validateRffs(servicos.getRffs(), warnings);
        validateCombustivel(servicos.getCombustivel(), warnings);
        validateManutencao(servicos.getManutencao(), warnings);
    }
    
    /**
     * Valida RFFS
     */
    private void validateRffs(RotaerData.RffsInfo rffs, List<ValidationWarning> warnings) {
        if (rffs == null) {
            return; // RFFS é opcional
        }
        
        Integer catCivil = rffs.getCatCivil();
        Integer catMil = rffs.getCatMil();
        
        if (catCivil != null && (catCivil < MIN_RFFS_CATEGORY || catCivil > MAX_RFFS_CATEGORY)) {
            warnings.add(new ValidationWarning("rffs.cat_civil", catCivil.toString(), "categoria_fora_limites", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Categoria civil fora dos limites (%d a %d)", MIN_RFFS_CATEGORY, MAX_RFFS_CATEGORY)));
        }
        
        if (catMil != null && (catMil < MIN_RFFS_CATEGORY || catMil > MAX_RFFS_CATEGORY)) {
            warnings.add(new ValidationWarning("rffs.cat_mil", catMil.toString(), "categoria_fora_limites", 
                                             ValidationWarning.Severity.WARNING, 
                                             String.format("Categoria militar fora dos limites (%d a %d)", MIN_RFFS_CATEGORY, MAX_RFFS_CATEGORY)));
        }
    }
    
    /**
     * Valida combustível
     */
    private void validateCombustivel(List<String> combustivel, List<ValidationWarning> warnings) {
        if (combustivel == null || combustivel.isEmpty()) {
            return; // Combustível é opcional
        }
        
        for (String tipo : combustivel) {
            String normalized = tipo.trim().toUpperCase();
            if (!normalized.matches("^(PF|TF|\\(m\\))$")) {
                warnings.add(new ValidationWarning("combustivel", tipo, "tipo_combustivel_nao_reconhecido", 
                                                 ValidationWarning.Severity.INFO, "Tipo de combustível não reconhecido: " + tipo));
            }
        }
    }
    
    /**
     * Valida manutenção
     */
    private void validateManutencao(List<String> manutencao, List<ValidationWarning> warnings) {
        if (manutencao == null || manutencao.isEmpty()) {
            return; // Manutenção é opcional
        }
        
        for (String tipo : manutencao) {
            String normalized = tipo.trim().toUpperCase();
            if (!normalized.matches("^(S[1-5])$")) {
                warnings.add(new ValidationWarning("manutencao", tipo, "tipo_manutencao_nao_reconhecido", 
                                                 ValidationWarning.Severity.INFO, "Tipo de manutenção não reconhecido: " + tipo));
            }
        }
    }
    
    /**
     * Valida INFOTEMP
     */
    private void validateInfotemp(List<RotaerData.InfotempInfo> infotemp, List<ValidationWarning> warnings) {
        if (infotemp == null || infotemp.isEmpty()) {
            return; // INFOTEMP é opcional
        }
        
        for (int i = 0; i < infotemp.size(); i++) {
            RotaerData.InfotempInfo info = infotemp.get(i);
            String prefix = "infotemp[" + i + "]";
            
            validateInfotempId(prefix, info.getId(), warnings);
            validateNatureza(prefix, info.getNatureza(), warnings);
        }
    }
    
    /**
     * Valida ID do INFOTEMP
     */
    private void validateInfotempId(String prefix, String id, List<ValidationWarning> warnings) {
        if (id == null || id.trim().isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".id", "empty", "infotemp_id_obrigatorio", 
                                             ValidationWarning.Severity.ERROR, "ID do INFOTEMP é obrigatório"));
            return;
        }
        
        if (!INFOTEMP_ID_PATTERN.matcher(id.trim().toUpperCase()).matches()) {
            warnings.add(new ValidationWarning(prefix + ".id", id, "infotemp_id_formato_invalido", 
                                             ValidationWarning.Severity.ERROR, "Formato do ID do INFOTEMP inválido: " + id));
        }
    }
    
    /**
     * Valida natureza do INFOTEMP
     */
    private void validateNatureza(String prefix, String natureza, List<ValidationWarning> warnings) {
        if (natureza == null || natureza.trim().isEmpty()) {
            warnings.add(new ValidationWarning(prefix + ".natureza", "empty", "natureza_obrigatoria", 
                                             ValidationWarning.Severity.ERROR, "Natureza do INFOTEMP é obrigatória"));
            return;
        }
        
        String normalized = natureza.trim().toUpperCase();
        if (!normalized.matches("^[FRCM]$")) {
            warnings.add(new ValidationWarning(prefix + ".natureza", natureza, "natureza_invalida", 
                                             ValidationWarning.Severity.ERROR, "Natureza inválida (deve ser F, R, M ou C): " + natureza));
        }
    }
}
