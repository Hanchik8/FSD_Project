package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(UserDto userDto, Model model) {
        try {
            userService.registerUser(userDto);
            model.addAttribute("message", "Пользователь успешно зарегистрирован!");
            return "login"; // редирект на страницу входа или возвращаем страницу login.html
        } catch (RuntimeException e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
    }


    @GetMapping("/register")
    public String registerPage() {
        return "register"; // имя файла register.html в resources/templates
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login"; // имя файла login.html
    }

}
