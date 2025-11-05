package com.lmello.dasto.shared.validators.decorators;

import com.lmello.dasto.shared.validators.implementations.MutuallyExclusiveValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MutuallyExclusiveValidator.class)
public @interface MutuallyExclusive {
    String message() default "Only one field can be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields();
}
