package com.jhome.user.dto;

import com.jhome.user.domain.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Getter
@ToString
@RequiredArgsConstructor
public class UserResponse {

    private final String username;
    private final String role;
    private final String type;
    private final String name;
    private final String email;
    private final String phone;
    private final String createAt;
    private final String updateAt;

    public static UserResponse of(UserEntity user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 24H:mm:ss");
        return new UserResponse(
                user.getUsername(),
                user.getRole(),
                user.getType(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt().format(formatter),
                user.getUpdatedAt().format(formatter));
    }

}
