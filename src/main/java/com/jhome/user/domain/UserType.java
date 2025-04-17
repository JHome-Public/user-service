package com.jhome.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {

    JHOME_USER(1, "JHOME"),
    OAUTH2_USER(2, "OAUTH");

    private final int seq;
    private final String name;

    public static String getUserTypeString(int value) {
        for (UserType type : values()) {
            if (type.getSeq() == value) {
                return type.getName();
            }
        }
        throw new IllegalArgumentException("Invalid UserType seq: " + value);
    }

}
