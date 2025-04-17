package com.jhome.user;

import com.jhome.user.domain.UserEntity;
import com.jhome.user.domain.UserStatus;
import com.jhome.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Active User 1
        userRepository.save(UserEntity.builder()
                .username("jhome")
                .name("jhome")
                .email("email@jhome.com")
                .status(UserStatus.ACTIVE)
                .build());

        // Active User 2
        userRepository.save(UserEntity.builder()
                .username("jhome2")
                .name("jhome2")
                .email("email2@jhome.com")
                .status(UserStatus.ACTIVE)
                .build());

        // Inactive User
        userRepository.save(UserEntity.builder()
                .username("unknown")
                .name("jhome")
                .email("email@jhome.com")
                .status(UserStatus.INACTIVE)
                .build());
    }

    @Test
    @DisplayName("existsByUsername - 활성 사용자 존재 여부 확인")
    void existsByUsernameAndStatus() {
        //given
        final String username1 = "jhome";
        final String username2 = "unknown";

        //when
        final boolean exists = userRepository.existsByUsername(username1);
        final boolean notExists = userRepository.existsByUsername(username2);

        //then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("findByUsernameAndStatus - 특정 사용자 정보 조회")
    void findByUsernameAndStatus() {
        //given
        final String username = "jhome";

        //when
        final Optional<UserEntity> user = userRepository.findByUsername(username);

        //then
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("email@jhome.com");
    }

    @Test
    @DisplayName("findAllByStatus - ACTIVE 상태의 사용자 페이징 조회")
    void findAllByStatus() {
        //given
        final Pageable pageable = PageRequest.of(0, 10);

        //when
        final Page<UserEntity> users = userRepository.findAll(pageable);

        //then
        assertThat(users.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("findByKeyword - 키워드 검색")
    void findByKeyword() {
        //given
        final String searchKeyword = "jhome2";
        final Pageable pageable = PageRequest.of(0, 10);

        //when
        final Page<UserEntity> users = userRepository.findByKeyword(searchKeyword, pageable);

        //then
        assertThat(users.getTotalElements()).isEqualTo(1);
    }
}
