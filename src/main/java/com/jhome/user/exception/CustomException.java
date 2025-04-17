package com.jhome.user.exception;

import com.jhome.user.response.ApiResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ApiResponseCode apiResponseCode;

    public CustomException(ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }

}
