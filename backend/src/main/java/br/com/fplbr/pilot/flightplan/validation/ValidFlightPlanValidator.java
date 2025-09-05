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
            return true; // @NotNull jÃ¡ deve tratar isso
        }

        // Verifica se Ã© um voo simplificado
        boolean isVooSimplificado = isVooSimplificado(flightPlan);
        
        // Valida a antecedÃªncia mÃ­nima
        if (!antecedenciaValidator.validarAntecedencia(flightPlan.getHoraPartida(), isVooSimplificado)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                antecedenciaValidator.getMensagemErro(isVooSimplificado)
            ).addPropertyNode("horaPartida").addConstraintViolation();
            return false;
        }

        // Valida os campos obrigatÃ³rios
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
     * Verifica se um voo Ã© completo com base nas regras:
     * 1. Cruzamento de fronteiras internacionais
     * 2. Voos sobre o oceano
     * 3. OperaÃ§Ãµes IFR
     * 
     * @param flightPlan Plano de voo a ser verificado
     * @return true se for um voo completo, false se for simplificado
     */
    private boolean isVooCompleto(FlightPlan flightPlan) {
        String aerodromoPartida = flightPlan.getAerodromoDePartida();
        String aerodromoDestino = flightPlan.getAerodromoDeDestino();
        
        if (aerodromoPartida == null || aerodromoPartida.isBlank() || 
            aerodromoDestino == null || aerodromoDestino.isBlank()) {
            return true; // Se nÃ£o tiver aerÃ³dromos, considera completo por seguranÃ§a
        }
        
        // 1. Verifica se Ã© um voo internacional
        boolean isInternacional = aerodromoUtil.isVooInternacional(
            aerodromoPartida, 
            aerodromoDestino
        );
        
        // 2. Verifica se Ã© um voo sobre o oceano
        boolean isSobreOceano = aerodromoUtil.isVooSobreOceano(
            aerodromoPartida,
            aerodromoDestino
        );
        
        // 3. Verifica se Ã© uma operaÃ§Ã£o IFR
        boolean isIFR = flightPlan.getRegraDeVooEnum() != null && 
                       flightPlan.getRegraDeVooEnum().isIFR();
        
        // O segundo aerÃ³dromo de alternativa Ã© opcional e nÃ£o afeta a classificaÃ§Ã£o do voo
        
        // Ã‰ um voo completo se atender a qualquer um dos critÃ©rios
        return isInternacional || isSobreOceano || isIFR;
    }
    
    /**
     * Verifica se um voo Ã© simplificado (oposto de completo)
     */
    private boolean isVooSimplificado(FlightPlan flightPlan) {
        return !isVooCompleto(flightPlan);
    }
    
    /**
     * Valida os campos obrigatÃ³rios com base no tipo de voo
     */
    private List<String> validarCamposObrigatorios(FlightPlan flightPlan, boolean isVooSimplificado) {
        List<String> erros = new ArrayList<>();
        
        // Campos obrigatÃ³rios para todos os voos
        if (flightPlan.getIdentificacaoDaAeronave() == null || flightPlan.getIdentificacaoDaAeronave().isBlank()) {
            erros.add("IdentificaÃ§Ã£o da aeronave Ã© obrigatÃ³ria");
        }
        if (flightPlan.getRegraDeVooEnum() == null) {
            erros.add("Regra de voo Ã© obrigatÃ³ria");
        }
        if (flightPlan.getTipoDeVooEnum() == null) {
            erros.add("Tipo de voo Ã© obrigatÃ³rio");
        }
        if (flightPlan.getTipoDeAeronave() == null || flightPlan.getTipoDeAeronave().isBlank()) {
            erros.add("Tipo de aeronave Ã© obrigatÃ³rio");
        }
        if (flightPlan.getCategoriaEsteiraTurbulenciaEnum() == null) {
            erros.add("Categoria de esteira de turbulÃªncia Ã© obrigatÃ³ria");
        }
        if (flightPlan.getEquipamentoCapacidadeDaAeronave() == null) {
            erros.add("Equipamento e capacidade da aeronave sÃ£o obrigatÃ³rios");
        }
        if (flightPlan.getVigilancia() == null) {
            erros.add("VigilÃ¢ncia Ã© obrigatÃ³ria");
        }
        if (flightPlan.getAerodromoDePartida() == null || flightPlan.getAerodromoDePartida().isBlank()) {
            erros.add("AerÃ³dromo de partida Ã© obrigatÃ³rio");
        }
        if (flightPlan.getHoraPartida() == null) {
            erros.add("Hora de partida Ã© obrigatÃ³ria");
        }
        if (flightPlan.getAerodromoDeDestino() == null || flightPlan.getAerodromoDeDestino().isBlank()) {
            erros.add("AerÃ³dromo de destino Ã© obrigatÃ³rio");
        }
        
        // Verifica se Ã© necessÃ¡rio aerÃ³dromo de alternativa
        if (isVooCompleto(flightPlan)) {
            if (flightPlan.getAerodromoDeAlternativa() == null || flightPlan.getAerodromoDeAlternativa().isBlank()) {
                erros.add("AerÃ³dromo de alternativa Ã© obrigatÃ³rio para voos completos");
            }
            // O segundo aerÃ³dromo de alternativa Ã© opcional
        }
        
        if (flightPlan.getVelocidadeDeCruzeiro() == null || flightPlan.getVelocidadeDeCruzeiro().isBlank()) {
            erros.add("Velocidade de cruzeiro Ã© obrigatÃ³ria");
        }
        if (flightPlan.getNivelDeVoo() == null || flightPlan.getNivelDeVoo().isBlank()) {
            erros.add("NÃ­vel de voo Ã© obrigatÃ³rio");
        }
        if (flightPlan.getRota() == null || flightPlan.getRota().isBlank()) {
            erros.add("Rota Ã© obrigatÃ³ria");
        }
        if (flightPlan.getOutrasInformacoes() == null) {
            erros.add("Outras informaÃ§Ãµes sÃ£o obrigatÃ³rias");
        } else if (flightPlan.getOutrasInformacoes().isBlank()) {
            erros.add("Pelo menos um campo de 'Outras informaÃ§Ãµes' deve ser preenchido");
        }
        if (flightPlan.getDof() == null) {
            erros.add("DOF (Data Operacional do Voo) Ã© obrigatÃ³rio");
        }
        if (flightPlan.getInformacaoSuplementar() == null) {
            erros.add("InformaÃ§Ã£o suplementar Ã© obrigatÃ³ria");
        } else if (flightPlan.getInformacaoSuplementar().isBlank()) {
            erros.add("Pelo menos um campo de 'InformaÃ§Ã£o suplementar' deve ser preenchido");
        }
        
        // Se for voo completo, valida campos adicionais
        if (!isVooSimplificado) {
            // Adicione aqui validaÃ§Ãµes adicionais para voos completos, se necessÃ¡rio
            // Por exemplo, verificar se o segundo aerÃ³dromo de alternativa estÃ¡ preenchido
            if (flightPlan.getAerodromoDeAlternativaSegundo() == null || 
                flightPlan.getAerodromoDeAlternativaSegundo().isBlank()) {
                erros.add("Segundo aerÃ³dromo de alternativa Ã© obrigatÃ³rio para voos completos");
            }
            // Outras validaÃ§Ãµes especÃ­ficas para voos completos podem ser adicionadas aqui
        }
        
        return erros;
    }
}
