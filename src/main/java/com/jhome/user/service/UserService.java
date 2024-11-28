package com.jhome.user.service;

import com.jhome.user.common.response.ApiResponseCode;
import com.jhome.user.domain.UserEntity;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.common.exception.CustomException;
import com.jhome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponse addUser(JoinRequest request) {
        UserEntity newUser = UserEntity.createUser(request, bCryptPasswordEncoder);
        return UserResponse.of((saveUser(newUser)));
    }

    public UserResponse editUser(String username, EditRequest request) {
        UserEntity user = getUserOrThrow(username);
        user.edit(request, bCryptPasswordEncoder);
        return UserResponse.of(saveUser(user));
    }

    public List<UserResponse> getUserResponseList() {
        return getUserList().stream()
                .map(UserResponse::of)
                .toList();
    }

    public UserResponse getUserResponse(String username) {
        return UserResponse.of(getUserOrThrow(username));
    }

    public void deactivateUser(String username) {
        UserEntity user = getUserOrThrow(username);
        user.deactivate();
        saveUser(user);
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