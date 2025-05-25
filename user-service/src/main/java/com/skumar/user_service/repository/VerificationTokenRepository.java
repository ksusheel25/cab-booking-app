package com.skumar.user_service.repository;

import com.skumar.user_service.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType);
    void deleteByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType);
}