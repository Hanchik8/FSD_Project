package com.example.demo.controller;

import com.example.demo.entity.UserEntity;
import com.example.demo.service.GameStatsService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final UserService userService;
    private final GameStatsService gameStatsService;

    @GetMapping("/game")
    public String gamePage(Model model, Principal principal) {
        String username = principal.getName();
        UserEntity user = userService.getUserByUsername(username);
        var stats = gameStatsService.getStatsByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("capturedCount", stats.getCapturedCount());
        model.addAttribute("gamesPlayed", stats.getGamesPlayed());
        model.addAttribute("gamesWon", stats.getGamesWon());

        return "index";
    }
}
