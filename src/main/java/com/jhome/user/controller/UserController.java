package com.jhome.user.controller;

import com.jhome.user.dto.JoinRequest;
import com.jhome.user.response.ApiResponse;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.SearchRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping(UserUri.DEFAULT)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> join(
            @Valid @RequestBody final JoinRequest request
    ) {
        final UserResponse userResponse = userService.addUser(request);

        return ResponseEntity
                .created(URI.create("/api/users/"+userResponse.getUsername()))
                .body(ApiResponse.success(userResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list(
            @ModelAttribute final SearchRequest searchRequest
    ) {
        final Pageable pageable = searchRequest.createPageable();

        final Page<UserResponse> userResPage = userService.getUserResponsePage(searchRequest.getSearchKeyword(), pageable);

        return ResponseEntity
                .ok(ApiResponse.success(userResPage));
    }

    @GetMapping(UserUri.USERNAME_VARIABLE)
    public ResponseEntity<ApiResponse<?>> detail(
            @PathVariable final String username
    ) {
        final UserResponse userResponse = userService.getUserResponse(username);

        return ResponseEntity
                .ok(ApiResponse.success(userResponse));
    }

    @PutMapping(UserUri.USERNAME_VARIABLE)
    public ResponseEntity<ApiResponse<?>> edit(
            @PathVariable final String username,
            @Valid @RequestBody final EditRequest request
    ) {
        final UserResponse userResponse = userService.editUser(username, request);

        return ResponseEntity
                .ok(ApiResponse.success(userResponse));
    }

    @DeleteMapping(UserUri.USERNAME_VARIABLE)
    public ResponseEntity<ApiResponse<?>> leave(
            @PathVariable final String username
    ) {
        userService.deactivateUser(username);

        return ResponseEntity
                .ok(ApiResponse.success());
    }

}
