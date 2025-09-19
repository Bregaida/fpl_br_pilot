package br.com.fplbr.pilot.flightplan.application.service;

import br.com.fplbr.pilot.flightplan.application.dto.PlanoDeVooDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class FplEncoderService {
    
    private static final Logger LOG = Logger.getLogger(FplEncoderService.class);
    
    public String encode(PlanoDeVooDTO fp) {
        try {
            StringBuilder fpl = new StringBuilder();
            
            // Campo 7/8: FPL-REG-IFR/VFR
            fpl.append("(FPL-").append(fp.getIdentificacaoDaAeronave());
            fpl.append("-").append(fp.getRegraDeVooEnum());
            fpl.append("\n");
            
            // Campo 9: TYPE/W
            fpl.append(" -TYPE/").append(fp.getTipoDeAeronave());
            fpl.append("/").append(fp.getCategoriaEsteiraTurbulenciaEnum());
            fpl.append("\n");
            
            // Campo 10: Equipamento e Vigilância
            fpl.append(" -SURV/");
            if (fp.getVigilancia() != null && !fp.getVigilancia().isEmpty()) {
                fpl.append(String.join("", fp.getVigilancia()));
            }
            fpl.append(" EQPT/");
            if (fp.getEquipamentoCapacidadeDaAeronave() != null && !fp.getEquipamentoCapacidadeDaAeronave().isEmpty()) {
                fpl.append(String.join("", fp.getEquipamentoCapacidadeDaAeronave()));
            }
            fpl.append("\n");
            
            // Campo 13: Partida + hora
            if (fp.getAerodromoDePartida() != null && fp.getHoraPartida() != null) {
                String horaPartida = formatHoraPartida(fp.getHoraPartida());
                fpl.append(" -").append(fp.getAerodromoDePartida()).append(horaPartida);
                fpl.append("\n");
            }
            
            // Campo 15: Velocidade/Nível/Rota
            if (fp.getVelocidadeDeCruzeiro() != null && fp.getNivelDeVoo() != null) {
                fpl.append(" -").append(fp.getVelocidadeDeCruzeiro());
                fpl.append(fp.getNivelDeVoo());
                if (fp.getRota() != null && !fp.getRota().trim().isEmpty()) {
                    fpl.append(" ").append(sanitizeRota(fp.getRota()));
                }
                fpl.append("\n");
            }
            
            // Campo 16: Destino + EET
            if (fp.getAerodromoDeDestino() != null) {
                fpl.append(" -").append(fp.getAerodromoDeDestino());
                String eet = fp.getTempoDeVooPrevisto() != null ? fp.getTempoDeVooPrevisto() : "0000";
                fpl.append(eet);
                fpl.append("\n");
            }
            
            // Campo 19: Informações Suplementares
            if (fp.getInformacaoSuplementar() != null) {
                PlanoDeVooDTO.InformacaoSuplementar info = fp.getInformacaoSuplementar();
                
                // E/ - Autonomia
                if (info.getAutonomia() != null && !info.getAutonomia().trim().isEmpty()) {
                    fpl.append(" -E/").append(info.getAutonomia());
                }
                
                // P/ - Pessoas a bordo
                if (info.getPob() != null) {
                    fpl.append(" P/").append(String.format("%03d", info.getPob()));
                }
                
                // R/ - Rádio emergência
                if (info.getRadioEmergencia() != null && !info.getRadioEmergencia().isEmpty()) {
                    fpl.append(" R/").append(String.join("", info.getRadioEmergencia()));
                }
                
                // S/ - Sobrevivência
                if (info.getSobrevivencia() != null && !info.getSobrevivencia().isEmpty()) {
                    fpl.append(" S/").append(String.join("", info.getSobrevivencia()));
                }
                
                // J/ - Coletes
                if (info.getColetes() != null && !info.getColetes().isEmpty()) {
                    fpl.append(" J/").append(String.join("", info.getColetes()));
                }
                
                // D/ - Botes
                if (info.getBotes() != null && Boolean.TRUE.equals(info.getBotes().getPossui())) {
                    fpl.append(" D/");
                    if (info.getBotes().getNumero() != null) {
                        fpl.append(info.getBotes().getNumero());
                    }
                    if (info.getBotes().getCapacidade() != null) {
                        fpl.append("/").append(info.getBotes().getCapacidade());
                    }
                    if (Boolean.TRUE.equals(info.getBotes().getC())) {
                        fpl.append("/C");
                    }
                }
                
                // A/ - Cor e marca
                if (info.getCorEMarcaAeronave() != null && !info.getCorEMarcaAeronave().trim().isEmpty()) {
                    fpl.append(" A/").append(info.getCorEMarcaAeronave());
                }
                
                // C/ - Telefone (frequência de emergência)
                if (info.getTelefone() != null && !info.getTelefone().trim().isEmpty()) {
                    fpl.append(" C/").append(info.getTelefone());
                }
                
                fpl.append("\n");
            }
            
            // Campo 18: Outras informações
            if (fp.getOutrasInformacoes() != null) {
                PlanoDeVooDTO.OutrasInformacoes outras = fp.getOutrasInformacoes();
                
                // DOF/ - Data de voo
                if (outras.getDof() != null && !outras.getDof().trim().isEmpty()) {
                    fpl.append(" -DOF/").append(outras.getDof());
                }
                
                // PBN/ - Performance Based Navigation (se aplicável)
                // NAV/ - Navegação (se aplicável)
                // COM/ - Comunicação (se aplicável)
                // REG/ - Registro (se aplicável)
                // EET/ - Estimated Elapsed Time (se aplicável)
                if (outras.getEet() != null && !outras.getEet().trim().isEmpty()) {
                    fpl.append(" EET/").append(outras.getEet());
                }
                
                // OPR/ - Operador
                if (outras.getOpr() != null && !outras.getOpr().trim().isEmpty()) {
                    fpl.append(" OPR/").append(outras.getOpr());
                }
                
                // ALTN/ - Aeródromo de alternativa
                if (fp.getAerodromoDeAlternativa() != null && !fp.getAerodromoDeAlternativa().trim().isEmpty()) {
                    fpl.append(" ALTN/").append(fp.getAerodromoDeAlternativa());
                }
                
                // DEP/ - Aeródromo de partida (se diferente do campo 13)
                if (outras.getFrom() != null && !outras.getFrom().trim().isEmpty() && 
                    !outras.getFrom().equals(fp.getAerodromoDePartida())) {
                    fpl.append(" DEP/").append(outras.getFrom());
                }
                
                // RMK/ - Observações (por último)
                if (outras.getRmk() != null && !outras.getRmk().trim().isEmpty()) {
                    fpl.append(" RMK/").append(sanitizeRmk(outras.getRmk()));
                }
                
                fpl.append("\n");
            }
            
            fpl.append(")");
            
            String result = fpl.toString();
            LOG.debug("FPL gerado: " + result);
            return result;
            
        } catch (Exception e) {
            LOG.error("Erro ao gerar FPL: " + e.getMessage(), e);
            return "(FPL-ERRO-Ao gerar mensagem FPL)";
        }
    }
    
    private String formatHoraPartida(OffsetDateTime horaPartida) {
        if (horaPartida == null) return "";
        return horaPartida.format(DateTimeFormatter.ofPattern("HHmm"));
    }
    
    private String sanitizeRota(String rota) {
        if (rota == null) return "";
        return rota.trim().replaceAll("\\s+", " ");
    }
    
    private String sanitizeRmk(String rmk) {
        if (rmk == null) return "";
        return rmk.trim().replaceAll("\\s+", " ");
    }
}
