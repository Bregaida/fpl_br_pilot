package br.com.fplbr.pilot.flightplan.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * AnotaÃƒÂ§ÃƒÂ£o para validaÃƒÂ§ÃƒÂ£o de plano de voo.
 * 
 * <p>Esta anotaÃƒÂ§ÃƒÂ£o valida se um plano de voo atende a todas as regras de negÃƒÂ³cio,
 * incluindo a antecedÃƒÂªncia mÃƒÂ­nima para decolagem e a completude dos campos obrigatÃƒÂ³rios
 * com base no tipo de voo (simplificado ou completo).</p>
 * 
 * <p>Um voo ÃƒÂ© considerado simplificado quando ÃƒÂ© entre aerÃƒÂ³dromos terminais e com distÃƒÂ¢ncia
 * menor ou igual a 26 NM. Demais casos sÃƒÂ£o considerados voos completos.</p>
 * 
 * <p>Para voos simplificados, a antecedÃƒÂªncia mÃƒÂ­nima ÃƒÂ© de 15 minutos.
 * Para voos completos, a antecedÃƒÂªncia mÃƒÂ­nima ÃƒÂ© de 30 minutos.</p>
 */
@Documented
@Constraint(validatedBy = ValidFlightPlanValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlightPlan {
    /**
     * Mensagem de erro padrÃƒÂ£o
     */
    String message() default "Dados do plano de voo invÃƒÂ¡lidos. Verifique os campos obrigatÃƒÂ³rios e as regras de antecedÃƒÂªncia.";
    
    /**
     * Grupos de validaÃƒÂ§ÃƒÂ£o
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload para transporte de metadados adicionais
     */
    Class<? extends Payload>[] payload() default {};
}
