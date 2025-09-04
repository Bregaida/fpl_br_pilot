package br.com.fplbr.pilot.validation;

import br.com.fplbr.pilot.model.FlightPlan;
import br.com.fplbr.pilot.util.AerodromeUtil;
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
    AerodromeUtil aerodromeUtil;

    @Override
    public void initialize(ValidFlightPlan constraintAnnotation) {
        // Nada a inicializar
    }

    @Override
    public boolean isValid(FlightPlan flightPlan, ConstraintValidatorContext context) {
        if (flightPlan == null) {
            return true; // @NotNull já deve tratar isso
        }

        // Verifica se é um voo simplificado
        boolean isVooSimplificado = isVooSimplificado(flightPlan);
        
        // Valida a antecedência mínima
        if (!antecedenciaValidator.validarAntecedencia(flightPlan.getHoraPartida(), isVooSimplificado)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                antecedenciaValidator.getMensagemErro(isVooSimplificado)
            ).addPropertyNode("horaPartida").addConstraintViolation();
            return false;
        }

        // Valida os campos obrigatórios
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
     * Verifica se um voo é completo com base nas regras:
     * 1. Cruzamento de fronteiras internacionais
     * 2. Voos sobre o oceano
     * 3. Operações IFR
     * 
     * @param flightPlan Plano de voo a ser verificado
     * @return true se for um voo completo, false se for simplificado
     */
    private boolean isVooCompleto(FlightPlan flightPlan) {
        if (flightPlan.getAerodromoDePartida() == null || flightPlan.getAerodromoDeDestino() == null) {
            return true; // Se não tiver aeródromos, considera completo por segurança
        }
        
        // 1. Verifica se é um voo internacional
        boolean isInternacional = aerodromeUtil.isVooInternacional(
            flightPlan.getAerodromoDePartida(), 
            flightPlan.getAerodromoDeDestino()
        );
        
        // 2. Verifica se é um voo sobre o oceano
        boolean isSobreOceano = aerodromeUtil.isVooSobreOceano(
            flightPlan.getAerodromoDePartida(),
            flightPlan.getAerodromoDeDestino()
        );
        
        // 3. Verifica se é uma operação IFR
        boolean isIFR = flightPlan.getRegraDeVooEnum() != null && 
                       flightPlan.getRegraDeVooEnum().isIFR();
        
        // O segundo aeródromo de alternativa é opcional e não afeta a classificação do voo
        
        // É um voo completo se atender a qualquer um dos critérios
        return isInternacional || isSobreOceano || isIFR;
    }
    
    /**
     * Verifica se um voo é simplificado (oposto de completo)
     */
    private boolean isVooSimplificado(FlightPlan flightPlan) {
        return !isVooCompleto(flightPlan);
    }
    
    /**
     * Valida os campos obrigatórios com base no tipo de voo
     */
    private List<String> validarCamposObrigatorios(FlightPlan flightPlan, boolean isVooSimplificado) {
        List<String> erros = new ArrayList<>();
        
        // Campos obrigatórios para todos os voos
        if (flightPlan.getIdentificacaoDaAeronave() == null || flightPlan.getIdentificacaoDaAeronave().isBlank()) {
            erros.add("Identificação da aeronave é obrigatória");
        }
        if (flightPlan.getRegraDeVooEnum() == null) {
            erros.add("Regra de voo é obrigatória");
        }
        if (flightPlan.getTipoDeVooEnum() == null) {
            erros.add("Tipo de voo é obrigatório");
        }
        if (flightPlan.getTipoDeAeronave() == null) {
            erros.add("Tipo de aeronave é obrigatório");
        }
        if (flightPlan.getCategoriaEsteiraTurbulenciaEnum() == null) {
            erros.add("Categoria de esteira de turbulência é obrigatória");
        }
        if (flightPlan.getEquipamentoCapacidadeDaAeronave() == null) {
            erros.add("Equipamento e capacidade da aeronave são obrigatórios");
        }
        if (flightPlan.getVigilancia() == null) {
            erros.add("Vigilância é obrigatória");
        }
        if (flightPlan.getAerodromoDePartida() == null || flightPlan.getAerodromoDePartida().isBlank()) {
            erros.add("Aeródromo de partida é obrigatório");
        }
        if (flightPlan.getHoraPartida() == null) {
            erros.add("Hora de partida é obrigatória");
        }
        if (flightPlan.getAerodromoDeDestino() == null || flightPlan.getAerodromoDeDestino().isBlank()) {
            erros.add("Aeródromo de destino é obrigatório");
        }
        
        // Verifica se é necessário aeródromo de alternativa
        if (isVooCompleto(flightPlan)) {
            if (flightPlan.getAerodromoDeAlternativa() == null || flightPlan.getAerodromoDeAlternativa().isBlank()) {
                erros.add("Aeródromo de alternativa é obrigatório para voos completos");
            }
            // O segundo aeródromo de alternativa é opcional
        }
        
        if (flightPlan.getVelocidadeDeCruzeiro() == null || flightPlan.getVelocidadeDeCruzeiro().isBlank()) {
            erros.add("Velocidade de cruzeiro é obrigatória");
        }
        if (flightPlan.getNivelDeVoo() == null || flightPlan.getNivelDeVoo().isBlank()) {
            erros.add("Nível de voo é obrigatório");
        }
        if (flightPlan.getRota() == null || flightPlan.getRota().isBlank()) {
            erros.add("Rota é obrigatória");
        }
        if (flightPlan.getOutrasInformacoes() == null) {
            erros.add("Outras informações são obrigatórias");
        } else if (flightPlan.getOutrasInformacoes().isBlank()) {
            erros.add("Pelo menos um campo de 'Outras informações' deve ser preenchido");
        }
        if (flightPlan.getDof() == null) {
            erros.add("DOF (Data Operacional do Voo) é obrigatório");
        }
        if (flightPlan.getInformacaoSuplementar() == null) {
            erros.add("Informação suplementar é obrigatória");
        } else if (flightPlan.getInformacaoSuplementar().isBlank()) {
            erros.add("Pelo menos um campo de 'Informação suplementar' deve ser preenchido");
        }
        
        // Se for voo completo, valida campos adicionais
        if (!isVooSimplificado) {
            // Adicione aqui validações adicionais para voos completos, se necessário
            // Por exemplo, verificar se o segundo aeródromo de alternativa está preenchido
            if (flightPlan.getAerodromoDeAlternativaSegundo() == null || 
                flightPlan.getAerodromoDeAlternativaSegundo().isBlank()) {
                erros.add("Segundo aeródromo de alternativa é obrigatório para voos completos");
            }
            // Outras validações específicas para voos completos podem ser adicionadas aqui
        }
        
        return erros;
    }
}
