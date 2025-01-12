package com.example.picbooker.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import com.example.picbooker.RegexPatterns;
import com.example.picbooker.security.OauthToken.OauthProviderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Validated
@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name must not be blank")
    @Column(length = 100, name = "name", unique = true)
    private String username;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    @Pattern(regexp = RegexPatterns.emailRegex, message = "Email must be a valid format")
    private String email;

    @Column
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = RegexPatterns.passwordRegex, message = "Password must contain both letters and numbers")
    private String password;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime registerDate;

    @Column
    private String accessToken;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime expiresAt;

    @Column
    @Enumerated(EnumType.STRING)
    private OauthProviderType provider;

    @Column
    private String avatarUrl;

    @Column
    private Double latitude;
    @Column
    private Double longitude;

    @Column
    @Default
    private Boolean isEnabled = false;

    // temporary 2fa code needed while waiting for the user to enter it
    @Column
    private String temp2FACode;

    // 2fa code expiry time
    @Column
    private LocalDateTime codeExpiryTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // currently no authoritites/roles
        return new ArrayList<>();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User that = (User) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
