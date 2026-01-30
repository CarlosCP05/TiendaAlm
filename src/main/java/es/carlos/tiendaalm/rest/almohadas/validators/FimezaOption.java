package es.carlos.tiendaalm.rest.almohadas.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FirmezaOptionValidator.class)
@Target( {ElementType.FIELD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface FimezaOption {
    String message() default "Firmeza no válida tiene que ser: Muy_blanda, Blanda, Normal, Dura o Muy_dura";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
