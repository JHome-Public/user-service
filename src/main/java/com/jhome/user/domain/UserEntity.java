package com.jhome.user.domain;

import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.JoinRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity(name = "JHOME_USER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    private String role;
    private String type;

    private String name;
    private String email;
    private String phone;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @PrePersist
    public void prePersist() {
        if (this.userStatus == null) {
            this.userStatus = UserStatus.ACTIVE;
        }
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }

    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
    }

    public void edit(EditRequest request, PasswordEncoder encoder) {
        if (request.getUsername() != null) {
            this.username = request.getUsername();
        }
        if (request.getPassword() != null) {
            this.password = request.getPassword();
            this.encodePassword(encoder);
        }
        if (request.getRole() != null) {
            this.role = request.getRole();
        }
        if (request.getType() != null) {
            this.type = request.getType();
        }
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }
        if (request.getPhone() != null) {
            this.phone = request.getPhone();
        }
    }

    public static UserEntity createUser(JoinRequest request, PasswordEncoder encoder) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        user.encodePassword(encoder);

        return user;
    }
}
