package com.example.demo;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner dataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if (!userRepository.existsByUsername("admin")) {
//                UserEntity admin = UserEntity.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin123"))
//                        .build();
//                userRepository.save(admin);
//                System.out.println("Создан дефолтный пользователь: admin / admin123");
//            }
//        };
//    }
}
