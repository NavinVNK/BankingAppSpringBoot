package com.bank.config;

import com.bank.entity.User;
import com.bank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("clerk1")) {
            userRepository.save(new User("clerk1", passwordEncoder.encode("clerkPass123"), Set.of("ROLE_CLERK")));
        }
        if (!userRepository.existsByUsername("admin1")) {
            userRepository.save(new User("admin1", passwordEncoder.encode("adminPass123"), Set.of("ROLE_ADMIN", "ROLE_CLERK")));
        }
        if (!userRepository.existsByUsername("customer1")) {
            userRepository.save(new User("customer1", passwordEncoder.encode("customerPass123"), Set.of("ROLE_CUSTOMER")));
        }
    }
}