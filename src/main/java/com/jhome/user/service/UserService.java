package com.jhome.user.service;

import com.jhome.user.property.DatetimeProperty;
import com.jhome.user.response.ApiResponseCode;
import com.jhome.user.domain.UserEntity;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.exception.CustomException;
import com.jhome.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DatetimeProperty datetimeProperty;

    @Transactional
    public UserResponse addUser(final JoinRequest request) {
        validateUserExist(request);
        return toUserResponse(userRepository.save(UserEntity.createUser(request)));
    }

    public Page<UserResponse> getUserResponsePage(final String searchKeyword, final Pageable pageable) {
        Page<UserEntity> userPage;
        if (searchKeyword == null || searchKeyword.isBlank()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = Optional.ofNullable(userRepository.findByKeyword(searchKeyword, pageable))
                    .orElse(Page.empty(pageable));
        }

        return userPage.map(this::toUserResponse);
    }

    public UserResponse getUserResponse(final String username) {
        return toUserResponse(getUserOrThrow(username));
    }

    @Transactional
    public UserResponse editUser(final String username, final EditRequest request) {
        final UserEntity user = getUserOrThrow(username);
        user.edit(request);
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse deactivateUser(final String username) {
        final UserEntity user = getUserOrThrow(username);
        user.deactivate();
        return toUserResponse(userRepository.save(user));
    }

    public void validateUserExist(final JoinRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ApiResponseCode.USER_ALREADY_EXIST);
        }
    }

    public UserEntity getUserOrThrow(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ApiResponseCode.USER_NOT_FOUND));
    }

    public UserResponse toUserResponse(final UserEntity user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .type(user.getType())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .picture(user.getPicture())
                .createAt(datetimeProperty.formatToString(user.getCreatedAt()))
                .updateAt(datetimeProperty.formatToString(user.getUpdatedAt()))
                .status(user.getStatus().toString())
                .build();
    }

}