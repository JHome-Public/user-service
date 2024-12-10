package com.jhome.user.controller;

import com.jhome.user.common.exception.CustomException;
import com.jhome.user.common.response.ApiResponseCode;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.common.response.ApiResponse;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* Controller 역할
* - 요청 파라미터 검증
* - 에러 처리
* - 서비스 호출
* - 응답 처리
*/
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> join(
            @Valid @RequestBody JoinRequest request) {
        Boolean isExists = userService.isExistUser(request.getUsername());
        if(isExists) {
            throw new CustomException(ApiResponseCode.USER_ALREADY_EXIST);
        }

        UserResponse userResponse = userService.addUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list() {
        List<UserResponse> userResList = userService.getUserResponseList();

        return ResponseEntity
                .ok(ApiResponse.success(userResList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> detail(
            @PathVariable("id") String username) {
        UserResponse userResponse = userService.getUserResponse(username);

        return ResponseEntity
                .ok(ApiResponse.success(userResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> edit(
            @PathVariable("id") String username,
            @Valid @RequestBody EditRequest request) {
        UserResponse userResponse = userService.editUser(username, request);

        return ResponseEntity
                .ok(ApiResponse.success(userResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> leave(
            @PathVariable("id") String username) {
        userService.deactivateUser(username);

        return ResponseEntity
                .ok(ApiResponse.success());
    }

}
