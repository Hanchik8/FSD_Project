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

    public TokenEntity generateToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7); // токен действителен 7 дней
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .expiryDate(expiryDate)
                .user(user)
                .build();
        return tokenRepository.save(tokenEntity);
    }
}
