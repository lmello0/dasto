package com.lmello.dasto.shared.validators.implementations;

import com.lmello.dasto.shared.validators.decorators.MutuallyExclusive;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class MutuallyExclusiveValidator implements ConstraintValidator<MutuallyExclusive, Object> {
    private String[] fields;

    @Override
    public void initialize(MutuallyExclusive constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            int nonNullCount = 0;

            for (String fieldName : fields) {
                Field field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(value);

                if (fieldValue != null) {
                    nonNullCount++;

                    if (nonNullCount > 1) {
                        break;
                    }
                }
            }

            if (nonNullCount != 1) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Exactly one of [" + String.join(", ", fields) + "] must be provided"
                ).addConstraintViolation();

                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
