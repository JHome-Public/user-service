package com.jhome.user.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserTypeValidator.class)
@Documented
public @interface UserTypeLimit {
    String message() default "Invalid UserType value. Must be between 1 and the max UserType count.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
