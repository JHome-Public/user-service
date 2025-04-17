package com.jhome.user.exception;

import com.jhome.user.response.ApiResponse;
import com.jhome.user.response.ApiResponseCode;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DB 관련 예외 처리
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessExceptions(DataAccessException e) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.fail(ApiResponseCode.DATA_ACCESS_ERROR));
    }

    // 파라미터 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ApiResponseCode.REQUEST_ARGS_INVALID, errors));
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomExceptions(CustomException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(e.getApiResponseCode()));
    }

    // 위에서 처리되지 않은 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ApiResponseCode.FAIL));
    }

}
