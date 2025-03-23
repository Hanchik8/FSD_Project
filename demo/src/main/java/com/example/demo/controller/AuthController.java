package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@Valid UserDto userDto,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "register";
        }

        try {
            userService.registerUser(userDto);
            model.addAttribute("message", "Успешная регистрация!");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("error", e.getMessage());
            return "register";
        }

    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto()); // Важно для инициализации формы
        return "register";
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
