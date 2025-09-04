package br.com.fplbr.pilot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinutosMinimosAntecedenciaValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinutosMinimosAntecedencia {
    String message() default "Hora de partida inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    /**
     * Minutos mínimos de antecedência para voos completos (45 minutos)
     */
    int minutosCompleto() default 45;
    /**
     * Minutos mínimos de antecedência para voos simplificados (15 minutos)
     */
    int minutosSimplificado() default 15;
}
