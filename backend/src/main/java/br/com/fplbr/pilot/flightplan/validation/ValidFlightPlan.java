package br.com.fplbr.pilot.flightplan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * AnotaÃ§Ã£o para validaÃ§Ã£o de plano de voo.
 * 
 * <p>Esta anotaÃ§Ã£o valida se um plano de voo atende a todas as regras de negÃ³cio,
 * incluindo a antecedÃªncia mÃ­nima para decolagem e a completude dos campos obrigatÃ³rios
 * com base no tipo de voo (simplificado ou completo).</p>
 * 
 * <p>Um voo Ã© considerado simplificado quando Ã© entre aerÃ³dromos terminais e com distÃ¢ncia
 * menor ou igual a 26 NM. Demais casos sÃ£o considerados voos completos.</p>
 * 
 * <p>Para voos simplificados, a antecedÃªncia mÃ­nima Ã© de 15 minutos.
 * Para voos completos, a antecedÃªncia mÃ­nima Ã© de 30 minutos.</p>
 */
@Documented
@Constraint(validatedBy = ValidFlightPlanValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlightPlan {
    /**
     * Mensagem de erro padrÃ£o
     */
    String message() default "Dados do plano de voo invÃ¡lidos. Verifique os campos obrigatÃ³rios e as regras de antecedÃªncia.";
    
    /**
     * Grupos de validaÃ§Ã£o
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload para transporte de metadados adicionais
     */
    Class<? extends Payload>[] payload() default {};
}
