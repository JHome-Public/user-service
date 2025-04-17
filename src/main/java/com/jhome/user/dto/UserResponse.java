package com.jhome.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public final class UserResponse {

    private final String username;
    private final String password;
    private final String role;
    private final String type;
    private final String name;
    private final String email;
    private final String phone;
    private final String picture;
    private final String createAt;
    private final String updateAt;
    private final String status;

}
