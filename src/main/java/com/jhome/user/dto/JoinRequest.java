package com.jhome.user.dto;

import com.jhome.user.validator.PasswordPattern;
import com.jhome.user.validator.UserTypeLimit;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@ToString
public final class JoinRequest {

    @NotBlank(message = "Required Parameter Missing")
    @Size(min = 2, message = "username must be at least 2 characters long")
    private final String username;

    @ToString.Exclude
    @NotBlank(message = "Required Parameter Missing")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @PasswordPattern(message = "Password must contain at least one letter, one number, and one special character")
    private final String password;

    @NotBlank(message = "Required Parameter Missing")
    private final String name;

    @NotBlank(message = "Required Parameter Missing")
    @Email(message = "Invalid email format")
    private final String email;

    @NotNull(message = "Required Parameter Missing")
    @UserTypeLimit
    private final Integer userType;

}
