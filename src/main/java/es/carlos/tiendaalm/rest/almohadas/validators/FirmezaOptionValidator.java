package es.carlos.tiendaalm.rest.almohadas.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FirmezaOptionValidator implements ConstraintValidator<FimezaOption, String> {
    private final String[] validas = {"Muy_blanda", "Blanda", "Normal", "Dura", "Muy_dura"};
    @Override
    public void initialize(FimezaOption fimezaOption) {}

    @Override
    public boolean isValid(String firmezaField, ConstraintValidatorContext context) {
        if (firmezaField == null) return true;
        for (String valida : validas) if (firmezaField.equals(valida)) return true;
        return false;
    }
}
