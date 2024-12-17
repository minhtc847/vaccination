package com.vaccination.BE.repository;

import com.vaccination.BE.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime now);
    @Query("SELECT i.token FROM PasswordResetToken i WHERE i.email =:email and i.used=:used")
    String findByEmailAndUsed(@Param("email") String to, @Param("used") boolean b);
}
