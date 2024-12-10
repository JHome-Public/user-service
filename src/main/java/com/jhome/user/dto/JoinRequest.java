package com.jhome.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JoinRequest {

    @NotBlank(message = "Required Parameter Error")
    private String username;

    @NotBlank(message = "Required Parameter Error")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
