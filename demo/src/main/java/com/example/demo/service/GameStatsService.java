package com.example.demo.service;

import com.example.demo.entity.GameStatsEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.GameStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameStatsService {

    private final GameStatsRepository gameStatsRepository;

    public GameStatsEntity createStatsForUser(UserEntity user) {
        GameStatsEntity stats = GameStatsEntity.builder()
                .user(user)
                .gamesPlayed(0)
                .gamesWon(0)
                .capturedCount(0)
                .build();
        return gameStatsRepository.save(stats);
    }

    public GameStatsEntity incrementCaptured(String username) {
        GameStatsEntity stats = gameStatsRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("GameStats not found for user " + username));
        stats.setCapturedCount(stats.getCapturedCount() + 1);
        return gameStatsRepository.save(stats);
    }

    public GameStatsEntity getStatsByUsername(String username) {
        return gameStatsRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("GameStats not found for user " + username));
    }
}
