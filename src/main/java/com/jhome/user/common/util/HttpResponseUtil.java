package com.jhome.user.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhome.user.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HttpResponseUtil {

    private final ObjectMapper objectMapper;

    @Value("${spring.jwt.header}")
    private String header;

    @Value("${spring.jwt.prefix}")
    private String prefix;

    public void sendResponse(HttpServletResponse response, String token, HttpStatus status, ApiResponse<?> apiResponse) throws IOException {
        response.setHeader(header, prefix + token);
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

}
