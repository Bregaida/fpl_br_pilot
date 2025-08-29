package br.com.fplbr.pilot.fpl.aplicacao.validacao;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = br.com.fplbr.pilot.fpl.aplicacao.validacao.FplValidator.class)
public @interface ValidFpl {
  String message() default "Plano de voo invÃ¡lido (regras de consistÃªncia)";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}



