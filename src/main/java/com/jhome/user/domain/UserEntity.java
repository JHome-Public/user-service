package com.jhome.user.domain;

import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.JoinRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "JHOME_USER")
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
    private String picture;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public void encodePassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(this.password);
    }

    public void edit(EditRequest request) {
        if (request.getPassword() != null) {
            this.password = request.getPassword();
            this.encodePassword();
        }
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public static UserEntity createUser(JoinRequest request) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .email(request.getEmail())
                .type(UserType.getUserTypeString(request.getUserType()))
                .status(UserStatus.ACTIVE)
                .build();
        user.encodePassword();
        return user;
    }

}
