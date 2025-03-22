package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserDto userDto) {
        // Проверяем, нет ли пользователя с таким username
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        // Шифруем пароль
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // Создаём сущность пользователя
        UserEntity user = UserEntity.builder()
                .username(userDto.getUsername())
                .password(encodedPassword)
                .build();

        // Сохраняем в базе
        return userRepository.save(user);
    }
}
