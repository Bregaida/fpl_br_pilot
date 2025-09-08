package br.com.fplbr.pilot.flightplan.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Validador para garantir que a hora de partida tenha a antecedÃƒÂªncia mÃƒÂ­nima necessÃƒÂ¡ria.
 * Para voos simplificados: 15 minutos de antecedÃƒÂªncia
 * Para voos completos: 30 minutos de antecedÃƒÂªncia
 */
@ApplicationScoped
public class MinutosMinimosAntecedenciaValidator 
    implements ConstraintValidator<MinutosMinimosAntecedencia, LocalDateTime> {

    private int minutosCompleto;
    private int minutosSimplificado;

    /**
     * ObtÃƒÂ©m o tempo mÃƒÂ­nimo de antecedÃƒÂªncia para voos completos
     */
    public int getMinutosCompleto() {
        return minutosCompleto;
    }

    /**
     * ObtÃƒÂ©m o tempo mÃƒÂ­nimo de antecedÃƒÂªncia para voos simplificados
     */
    public int getMinutosSimplificado() {
        return minutosSimplificado;
    }

    @Override
    public void initialize(MinutosMinimosAntecedencia constraintAnnotation) {
        this.minutosCompleto = constraintAnnotation.minutosCompleto();
        this.minutosSimplificado = constraintAnnotation.minutosSimplificado();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        // Esta validaÃƒÂ§ÃƒÂ£o ÃƒÂ© feita no nÃƒÂ­vel de classe pelo ValidFlightPlanValidator
        return true;
    }

    /**
     * Valida se a data/hora de partida tem a antecedÃƒÂªncia mÃƒÂ­nima necessÃƒÂ¡ria
     * 
     * @param dataHoraPartida Data e hora de partida a ser validada
     * @param isVooSimplificado Indica se ÃƒÂ© um voo simplificado
     * @return true se a validaÃƒÂ§ÃƒÂ£o for bem-sucedida, false caso contrÃƒÂ¡rio
     */
    public boolean validarAntecedencia(LocalDateTime dataHoraPartida, boolean isVooSimplificado) {
        if (dataHoraPartida == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();
        int minutosMinimos = isVooSimplificado ? minutosSimplificado : minutosCompleto;
        LocalDateTime dataHoraMinima = agora.plusMinutes(minutosMinimos);
        
        return !dataHoraPartida.isBefore(dataHoraMinima);
    }
    
    /**
     * Retorna a mensagem de erro apropriada com base no tipo de voo
     */
    public String getMensagemErro(boolean isVooSimplificado) {
        int minutos = isVooSimplificado ? minutosSimplificado : minutosCompleto;
        return String.format("A hora de partida deve ter no mÃƒÂ­nimo %d minutos de antecedÃƒÂªncia", minutos);
    }
}
