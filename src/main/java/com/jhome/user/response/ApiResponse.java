package com.jhome.user.response;

import lombok.*;

@Getter
@Builder
@ToString
public class ApiResponse<D> {

    private final int code;
    private final String message;
    private final D data;

    public static ApiResponse<?> success() {
        return ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseCode.SUCCESS.getMessage())
                .build();
    }

    public static <D> ApiResponse<?> success(final D data) {
        return ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static ApiResponse<?> fail(final ApiResponseCode apiResponseCode) {
        return ApiResponse.builder()
                .code(apiResponseCode.getCode())
                .message(apiResponseCode.getMessage())
                .build();
    }

    public static <D> ApiResponse<?> fail(final ApiResponseCode apiResponseCode, final D data) {
        return ApiResponse.builder()
                .code(apiResponseCode.getCode())
                .message(apiResponseCode.getMessage())
                .data(data)
                .build();
    }

}
