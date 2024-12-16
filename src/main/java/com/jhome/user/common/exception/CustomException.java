package com.jhome.user.common.exception;

import com.jhome.user.common.response.ApiResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ApiResponseCode apiResponseCode;

    public CustomException(ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }

}
