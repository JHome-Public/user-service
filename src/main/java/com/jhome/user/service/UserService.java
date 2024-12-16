package com.jhome.user.service;

import com.jhome.user.common.property.DatetimeProperty;
import com.jhome.user.common.response.ApiResponseCode;
import com.jhome.user.domain.UserEntity;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.common.exception.CustomException;
import com.jhome.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DatetimeProperty datetimeProperty;

    @Transactional
    public UserResponse addUser(JoinRequest request) {
        UserEntity newUser = UserEntity.createUser(request, bCryptPasswordEncoder);
        return UserResponse.build((saveUser(newUser)), datetimeProperty);
    }

    @Transactional
    public UserResponse editUser(String username, EditRequest request) {
        UserEntity user = getUserOrThrow(username);
        user.edit(request, bCryptPasswordEncoder);
        return UserResponse.build(saveUser(user), datetimeProperty);
    }

    @Transactional
    public void deactivateUser(String username) {
        UserEntity user = getUserOrThrow(username);
        user.deactivate();
        saveUser(user);
    }

    public List<UserResponse> getUserResponseList() {
        return getUserList().stream()
                .map(user -> UserResponse.build(user, datetimeProperty))
                .toList();
    }

    public UserResponse getUserResponse(String username) {
        return UserResponse.build(getUserOrThrow(username), datetimeProperty);
    }

    public Boolean isExistUser(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public List<UserEntity> getUserList() {
        return userRepository.findAll();
    }

    public UserEntity getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ApiResponseCode.USER_NOT_FOUND));
    }

}