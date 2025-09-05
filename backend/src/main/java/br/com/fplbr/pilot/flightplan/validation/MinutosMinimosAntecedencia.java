package br.com.fplbr.pilot.flightplan.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

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
