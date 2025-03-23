package com.example.demo.filter;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    // Заменяем зависимость от UserService на UserRepository,
    // чтобы разорвать циклическую зависимость.
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Проверяем, выбрал ли пользователь опцию "Запомнить меня" (input с name="remember-me")
        String remember = request.getParameter("remember-me");
        if ("on".equals(remember)) {
            // Получаем username из объекта principal
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Загружаем UserEntity напрямую через UserRepository
            UserEntity userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Генерируем и сохраняем токен для пользователя
            var tokenEntity = tokenService.createTokenForUser(userEntity);

            // Создаём HttpOnly cookie с токеном для механизма "remember me"
            Cookie cookie = new Cookie("rememberMeToken", tokenEntity.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
            response.addCookie(cookie);
        }

        // Перенаправляем пользователя на страницу /game после успешной аутентификации
        response.sendRedirect("/game");
    }
}
