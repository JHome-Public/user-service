package com.jhome.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EditRequest {

    private String username;
    private String password;
    private String role;
    private String type;
    private String name;
    private String email;
    private String phone;

}
