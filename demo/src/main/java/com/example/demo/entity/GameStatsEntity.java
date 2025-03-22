package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int gamesPlayed;

    @Column(nullable = false)
    private int gamesWon;

    @Column(nullable = false)
    private int capturedCount;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
