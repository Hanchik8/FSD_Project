package com.example.demo.service;

import com.example.demo.entity.TokenEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    // Срок жизни токена (7 дней)
    private final long TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60;

    /**
     * Создаёт токен для указанного пользователя и сохраняет его в БД.
     */
    public TokenEntity createTokenForUser(UserEntity user) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(TOKEN_VALIDITY_SECONDS);

        TokenEntity tokenEntity = TokenEntity.builder()
                .token(tokenValue)
                .expiryDate(expiryDate)
                .user(user)
                .build();

        return tokenRepository.save(tokenEntity);
    }

    public TokenEntity findByToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    // Дополнительно, если нужно
    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public UserEntity getUserByToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(TokenEntity::getUser)
                .orElse(null);
    }
}
