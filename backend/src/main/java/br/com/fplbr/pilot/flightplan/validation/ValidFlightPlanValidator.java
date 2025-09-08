package br.com.fplbr.pilot.flightplan.validation;

import br.com.fplbr.pilot.flightplan.domain.model.FlightPlan;
import br.com.fplbr.pilot.aerodromos.util.AerodromoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ValidFlightPlanValidator implements ConstraintValidator<ValidFlightPlan, FlightPlan> {

    @Inject
    MinutosMinimosAntecedenciaValidator antecedenciaValidator;
    
    @Inject
    AerodromoUtil aerodromoUtil;

    @Override
    public void initialize(ValidFlightPlan constraintAnnotation) {
        // Nada a inicializar
    }

    @Override
    public boolean isValid(FlightPlan flightPlan, ConstraintValidatorContext context) {
        if (flightPlan == null) {
            return true; // @NotNull jÃƒÂ¡ deve tratar isso
        }

        // Verifica se ÃƒÂ© um voo simplificado
        boolean isVooSimplificado = isVooSimplificado(flightPlan);
        
        // Valida a antecedÃƒÂªncia mÃƒÂ­nima
        if (!antecedenciaValidator.validarAntecedencia(flightPlan.getHoraPartida(), isVooSimplificado)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                antecedenciaValidator.getMensagemErro(isVooSimplificado)
            ).addPropertyNode("horaPartida").addConstraintViolation();
            return false;
        }

        // Valida os campos obrigatÃƒÂ³rios
        List<String> erros = validarCamposObrigatorios(flightPlan, isVooSimplificado);
        
        if (!erros.isEmpty()) {
            context.disableDefaultConstraintViolation();
            for (String erro : erros) {
                context.buildConstraintViolationWithTemplate(erro).addConstraintViolation();
            }
            return false;
        }

        return true;
    }

    /**
     * Verifica se um voo ÃƒÂ© completo com base nas regras:
     * 1. Cruzamento de fronteiras internacionais
     * 2. Voos sobre o oceano
     * 3. OperaÃƒÂ§ÃƒÂµes IFR
     * 
     * @param flightPlan Plano de voo a ser verificado
     * @return true se for um voo completo, false se for simplificado
     */
    private boolean isVooCompleto(FlightPlan flightPlan) {
        String aerodromoPartida = flightPlan.getAerodromoDePartida();
        String aerodromoDestino = flightPlan.getAerodromoDeDestino();
        
        if (aerodromoPartida == null || aerodromoPartida.isBlank() || 
            aerodromoDestino == null || aerodromoDestino.isBlank()) {
            return true; // Se nÃƒÂ£o tiver aerÃƒÂ³dromos, considera completo por seguranÃƒÂ§a
        }
        
        // 1. Verifica se ÃƒÂ© um voo internacional
        boolean isInternacional = aerodromoUtil.isVooInternacional(
            aerodromoPartida, 
            aerodromoDestino
        );
        
        // 2. Verifica se ÃƒÂ© um voo sobre o oceano
        boolean isSobreOceano = aerodromoUtil.isVooSobreOceano(
            aerodromoPartida,
            aerodromoDestino
        );
        
        // 3. Verifica se ÃƒÂ© uma operaÃƒÂ§ÃƒÂ£o IFR
        boolean isIFR = flightPlan.getRegraDeVooEnum() != null && 
                       flightPlan.getRegraDeVooEnum().isIFR();
        
        // O segundo aerÃƒÂ³dromo de alternativa ÃƒÂ© opcional e nÃƒÂ£o afeta a classificaÃƒÂ§ÃƒÂ£o do voo
        
        // Ãƒâ€° um voo completo se atender a qualquer um dos critÃƒÂ©rios
        return isInternacional || isSobreOceano || isIFR;
    }
    
    /**
     * Verifica se um voo ÃƒÂ© simplificado (oposto de completo)
     */
    private boolean isVooSimplificado(FlightPlan flightPlan) {
        return !isVooCompleto(flightPlan);
    }
    
    /**
     * Valida os campos obrigatÃƒÂ³rios com base no tipo de voo
     */
    private List<String> validarCamposObrigatorios(FlightPlan flightPlan, boolean isVooSimplificado) {
        List<String> erros = new ArrayList<>();
        
        // Campos obrigatÃƒÂ³rios para todos os voos
        if (flightPlan.getIdentificacaoDaAeronave() == null || flightPlan.getIdentificacaoDaAeronave().isBlank()) {
            erros.add("IdentificaÃƒÂ§ÃƒÂ£o da aeronave ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getRegraDeVooEnum() == null) {
            erros.add("Regra de voo ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getTipoDeVooEnum() == null) {
            erros.add("Tipo de voo ÃƒÂ© obrigatÃƒÂ³rio");
        }
        if (flightPlan.getTipoDeAeronave() == null || flightPlan.getTipoDeAeronave().isBlank()) {
            erros.add("Tipo de aeronave ÃƒÂ© obrigatÃƒÂ³rio");
        }
        if (flightPlan.getCategoriaEsteiraTurbulenciaEnum() == null) {
            erros.add("Categoria de esteira de turbulÃƒÂªncia ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getEquipamentoCapacidadeDaAeronave() == null) {
            erros.add("Equipamento e capacidade da aeronave sÃƒÂ£o obrigatÃƒÂ³rios");
        }
        if (flightPlan.getVigilancia() == null) {
            erros.add("VigilÃƒÂ¢ncia ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getAerodromoDePartida() == null || flightPlan.getAerodromoDePartida().isBlank()) {
            erros.add("AerÃƒÂ³dromo de partida ÃƒÂ© obrigatÃƒÂ³rio");
        }
        if (flightPlan.getHoraPartida() == null) {
            erros.add("Hora de partida ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getAerodromoDeDestino() == null || flightPlan.getAerodromoDeDestino().isBlank()) {
            erros.add("AerÃƒÂ³dromo de destino ÃƒÂ© obrigatÃƒÂ³rio");
        }
        
        // Verifica se ÃƒÂ© necessÃƒÂ¡rio aerÃƒÂ³dromo de alternativa
        if (isVooCompleto(flightPlan)) {
            if (flightPlan.getAerodromoDeAlternativa() == null || flightPlan.getAerodromoDeAlternativa().isBlank()) {
                erros.add("AerÃƒÂ³dromo de alternativa ÃƒÂ© obrigatÃƒÂ³rio para voos completos");
            }
            // O segundo aerÃƒÂ³dromo de alternativa ÃƒÂ© opcional
        }
        
        if (flightPlan.getVelocidadeDeCruzeiro() == null || flightPlan.getVelocidadeDeCruzeiro().isBlank()) {
            erros.add("Velocidade de cruzeiro ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getNivelDeVoo() == null || flightPlan.getNivelDeVoo().isBlank()) {
            erros.add("NÃƒÂ­vel de voo ÃƒÂ© obrigatÃƒÂ³rio");
        }
        if (flightPlan.getRota() == null || flightPlan.getRota().isBlank()) {
            erros.add("Rota ÃƒÂ© obrigatÃƒÂ³ria");
        }
        if (flightPlan.getOutrasInformacoes() == null) {
            erros.add("Outras informaÃƒÂ§ÃƒÂµes sÃƒÂ£o obrigatÃƒÂ³rias");
        } else if (flightPlan.getOutrasInformacoes().isBlank()) {
            erros.add("Pelo menos um campo de 'Outras informaÃƒÂ§ÃƒÂµes' deve ser preenchido");
        }
        if (flightPlan.getDof() == null) {
            erros.add("DOF (Data Operacional do Voo) ÃƒÂ© obrigatÃƒÂ³rio");
        }
        if (flightPlan.getInformacaoSuplementar() == null) {
            erros.add("InformaÃƒÂ§ÃƒÂ£o suplementar ÃƒÂ© obrigatÃƒÂ³ria");
        } else if (flightPlan.getInformacaoSuplementar().isBlank()) {
            erros.add("Pelo menos um campo de 'InformaÃƒÂ§ÃƒÂ£o suplementar' deve ser preenchido");
        }
        
        // Se for voo completo, valida campos adicionais
        if (!isVooSimplificado) {
            // Adicione aqui validaÃƒÂ§ÃƒÂµes adicionais para voos completos, se necessÃƒÂ¡rio
            // Por exemplo, verificar se o segundo aerÃƒÂ³dromo de alternativa estÃƒÂ¡ preenchido
            if (flightPlan.getAerodromoDeAlternativaSegundo() == null || 
                flightPlan.getAerodromoDeAlternativaSegundo().isBlank()) {
                erros.add("Segundo aerÃƒÂ³dromo de alternativa ÃƒÂ© obrigatÃƒÂ³rio para voos completos");
            }
            // Outras validaÃƒÂ§ÃƒÂµes especÃƒÂ­ficas para voos completos podem ser adicionadas aqui
        }
        
        return erros;
    }
}
