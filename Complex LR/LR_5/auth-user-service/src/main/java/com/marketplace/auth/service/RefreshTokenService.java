package com.marketplace.auth.service;

import com.marketplace.auth.exception.TokenRefreshException;
import com.marketplace.auth.model.RefreshToken;
import com.marketplace.auth.repository.RefreshTokenRepository;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final Long refreshTokenDurationMs = 604800000L;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository; this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) { return refreshTokenRepository.findByToken(token); }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired.");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) { return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get()); }
}
