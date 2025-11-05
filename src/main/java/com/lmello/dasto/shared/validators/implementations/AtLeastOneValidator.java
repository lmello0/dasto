package com.lmello.dasto.shared.validators.implementations;

import com.lmello.dasto.shared.validators.decorators.AtLeastOne;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneValidator implements ConstraintValidator<AtLeastOne, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastOne constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            for (String fieldName : fields) {
                Field field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(value);

                if (fieldValue != null) {
                    return true;
                }
            }

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "At least one of [" + String.join(", ", fields) + "] must be provided"
            ).addConstraintViolation();

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
