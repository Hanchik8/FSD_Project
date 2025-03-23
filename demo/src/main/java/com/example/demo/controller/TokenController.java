package com.example.demo.controller;

import com.example.demo.entity.UserEntity;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/api/generateToken")
    public ResponseEntity<String> generateToken(Principal principal, HttpServletResponse response) {
        String username = principal.getName();
        UserEntity user = userService.getUserByUsername(username);

        var tokenEntity = tokenService.createTokenForUser(user);

        Cookie cookie = new Cookie("rememberMeToken", tokenEntity.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(cookie);

        return ResponseEntity.ok(tokenEntity.getToken());
    }
}
