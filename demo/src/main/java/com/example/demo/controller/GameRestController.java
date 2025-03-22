package com.example.demo.controller;

import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameRestController {

    private final UserService userService;

    @PostMapping("/incrementCaptured")
    public ResponseEntity<Void> incrementCaptured(Principal principal) {
        String username = principal.getName();
        userService.incrementCaptured(username);
        return ResponseEntity.ok().build();
    }
}
