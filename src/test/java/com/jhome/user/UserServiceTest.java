package com.jhome.user;

import com.jhome.user.exception.CustomException;
import com.jhome.user.property.DatetimeProperty;
import com.jhome.user.response.ApiResponseCode;
import com.jhome.user.domain.UserEntity;
import com.jhome.user.domain.UserStatus;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.repository.UserRepository;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private DatetimeProperty datetimeProperty;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddUser_Success() {
        // given
        final JoinRequest joinRequest = JoinRequest.builder()
                .username("jhome")
                .email("email@jhome.com")
                .name("J Home")
                .password("password123!")
                .userType(1)
                .build();
        final UserEntity userEntity = UserEntity.createUser(joinRequest);

        when(userRepository.existsByUsername(joinRequest.getUsername())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(datetimeProperty.formatToString(any())).thenReturn("2025-02-11 10:00:00");

        // when
        final UserResponse response = userService.addUser(joinRequest);

        // then
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(UserEntity.class));  // save 메소드가 한 번 호출되는지 확인
    }

    @Test
    void testAddUser_UserAlreadyExists() {
        // given
        final JoinRequest joinRequest = JoinRequest.builder()
                .username("jhome")
                .email("email@jhome.com")
                .name("J Home")
                .password("password123!")
                .userType(1)
                .build();
        when(userRepository.existsByUsername(joinRequest.getUsername())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.addUser(joinRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ApiResponseCode.USER_ALREADY_EXIST.getMessage());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testGetUserResponsePage_Success() {
        // given
        final JoinRequest joinRequest = JoinRequest.builder()
                .username("jhome")
                .email("email@jhome.com")
                .name("J Home")
                .password("password123!")
                .userType(1)
                .build();
        final UserEntity userEntity = UserEntity.createUser(joinRequest);
        final Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // when
        final Page<UserResponse> responsePage = userService.getUserResponsePage("", Pageable.unpaged());

        // then
        assertThat(responsePage).isNotNull();
        assertThat(responsePage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetUserResponsePageWithKeyword_Success() {
        // given
        final Page<UserEntity> userPage = new PageImpl<>(List.of());
        when(userRepository.findByKeyword(anyString(), any(Pageable.class))).thenReturn(userPage);

        // when
        final Page<UserResponse> responsePage = userService.getUserResponsePage("no list", Pageable.unpaged());

        // then
        assertThat(responsePage).isNotNull();
        assertThat(responsePage.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testGetUserResponse_Success() {
        // given
        final UserEntity userEntity = getUserEntity();
        when(userRepository.findByUsername("jhome")).thenReturn(Optional.of(userEntity));

        // when
        final UserResponse response = userService.getUserResponse("jhome");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("jhome");
    }

    private static UserEntity getUserEntity() {
        final JoinRequest joinRequest = JoinRequest.builder()
                .username("jhome")
                .email("email@jhome.com")
                .name("J Home")
                .password("password123!")
                .userType(1)
                .build();
        final UserEntity userEntity = UserEntity.createUser(joinRequest);
        return userEntity;
    }

    @Test
    void testGetUserResponse_UserNotFound() {
        // given
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserResponse("unknownUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ApiResponseCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void testEditUser_Success() {
        // given
        final JoinRequest joinRequest = JoinRequest.builder()
                .username("jhome")
                .email("email@jhome.com")
                .name("J Home")
                .password("password123!")
                .userType(1)
                .build();
        final UserEntity userEntity = UserEntity.createUser(joinRequest);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        final EditRequest editRequest = EditRequest.builder()
                .email("newemail@jhome.com")
                .build();
        userEntity.edit(editRequest);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // when
        final UserResponse response = userService.editUser("jhome", editRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("newemail@jhome.com");

    }

    @Test
    void testDeactivateUser_Success() {
        // given
        UserEntity userEntity = getUserEntity();
        when(userRepository.findByUsername("jhome")).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // when
        final UserResponse response = userService.deactivateUser("jhome");

        // then
        assertThat(response.getStatus()).isEqualTo(UserStatus.INACTIVE.name());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testToUserResponse_Success() {
        // given
        UserEntity userEntity = getUserEntity();

        // when
        final UserResponse response = userService.toUserResponse(userEntity);

        // then
        assertThat(response).isNotNull();
    }
}
