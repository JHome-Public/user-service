package com.jhome.user.validator;

import com.jhome.user.domain.UserType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserTypeValidator implements ConstraintValidator<UserTypeLimit, Integer> {

    private static final int MIN_VALUE = 1;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // `@NotBlank`과 함께 사용하면 필수 값으로 처리됨
        }

        int maxValue = UserType.values().length;
        return value >= MIN_VALUE && value <= maxValue;
    }
}

