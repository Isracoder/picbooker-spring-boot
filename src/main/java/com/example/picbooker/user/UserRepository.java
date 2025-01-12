package com.example.picbooker.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        Optional<User> findByUsername(String username);

        @Modifying
        @Query("UPDATE User u SET u.accessToken = :accessToken, u.expiresAt = :expiresAt WHERE u.refreshToken = :refreshToken")
        void updateAccessToken(@Param("refreshToken") String refreshToken, @Param("accessToken") String accessToken,
                        @Param("expiresAt") LocalDateTime expiresAt);

}
