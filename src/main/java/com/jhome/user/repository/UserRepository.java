package com.jhome.user.repository;

import com.jhome.user.domain.UserEntity;
import com.jhome.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsernameAndStatus(String username, UserStatus status);
    default Boolean existsByUsername(String username) {
        return existsByUsernameAndStatus(username, UserStatus.ACTIVE);
    }

    Optional<UserEntity> findByUsernameAndStatus(String username, UserStatus status);
    default Optional<UserEntity> findByUsername(String username) {
        return findByUsernameAndStatus(username, UserStatus.ACTIVE);
    }

    Page<UserEntity> findAllByStatus(Pageable pageable, UserStatus status);
    default Page<UserEntity> findAll(Pageable pageable) {
        return findAllByStatus(pageable, UserStatus.ACTIVE);
    }

    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE u.username LIKE %:searchKeyword%")
    Page<UserEntity> findByKeyword(@Param("searchKeyword") String searchKeyword, Pageable pageable);

}
