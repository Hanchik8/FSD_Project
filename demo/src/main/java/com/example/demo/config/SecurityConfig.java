package com.example.demo.config;

import com.example.demo.filter.CustomAuthenticationSuccessHandler;
import com.example.demo.filter.CustomRememberMeAuthenticationFilter;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Внедряем только successHandler и tokenService
    private final CustomAuthenticationSuccessHandler successHandler;
    private final TokenService tokenService;

    // Убираем поле customUserDetailsService, чтобы не было циклической зависимости

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService customUserDetailsService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ без аутентификации к /register и /login
                        .requestMatchers("/register", "/login").permitAll()
                        // Остальные пути требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")             // Кастомная страница логина
                        .successHandler(successHandler)   // Кастомный обработчик при успехе
                        .defaultSuccessUrl("/game", true) // Куда перенаправлять после входа
                        .permitAll()
                )
                .logout(Customizer.withDefaults()); // logout на /logout по умолчанию

        // Добавляем кастомный фильтр "remember me" до UsernamePasswordAuthenticationFilter
        http.addFilterBefore(
                new CustomRememberMeAuthenticationFilter(tokenService, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Хэширование пароля (обязательно для безопасности)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customUserDetailsService(UserRepository userRepository) {
        return username -> {
            var userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return User.builder()
                    .username(userEntity.getUsername())
                    .password(userEntity.getPassword())
                    .roles("USER")
                    .build();
        };
    }
}
