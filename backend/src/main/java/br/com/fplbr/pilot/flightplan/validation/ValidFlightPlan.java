package br.com.fplbr.pilot.flightplan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotação para validação de plano de voo.
 * 
 * <p>Esta anotação valida se um plano de voo atende a todas as regras de negócio,
 * incluindo a antecedência mínima para decolagem e a completude dos campos obrigatórios
 * com base no tipo de voo (simplificado ou completo).</p>
 * 
 * <p>Um voo é considerado simplificado quando é entre aeródromos terminais e com distância
 * menor ou igual a 26 NM. Demais casos são considerados voos completos.</p>
 * 
 * <p>Para voos simplificados, a antecedência mínima é de 15 minutos.
 * Para voos completos, a antecedência mínima é de 30 minutos.</p>
 */
@Documented
@Constraint(validatedBy = ValidFlightPlanValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlightPlan {
    /**
     * Mensagem de erro padrão
     */
    String message() default "Dados do plano de voo inválidos. Verifique os campos obrigatórios e as regras de antecedência.";
    
    /**
     * Grupos de validação
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload para transporte de metadados adicionais
     */
    Class<? extends Payload>[] payload() default {};
}
