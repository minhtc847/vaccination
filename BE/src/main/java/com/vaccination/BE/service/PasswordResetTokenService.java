package com.vaccination.BE.service;

import com.vaccination.BE.entity.PasswordResetToken;
import com.vaccination.BE.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    public PasswordResetToken createToken(String email) {
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        return tokenRepository.save(token);
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isPresent()) {
            PasswordResetToken resetToken = optionalToken.get();
            if (!resetToken.isUsed() && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                resetToken.setUsed(true);
                tokenRepository.save(resetToken);
                return true;
            }
        }
        return false;
    }

    public String getEmailByToken(String token) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);
        return optionalToken.map(PasswordResetToken::getEmail).orElse(null);
    }
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
