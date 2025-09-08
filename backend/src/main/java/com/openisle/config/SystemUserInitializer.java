package com.openisle.config;

import com.openisle.model.Role;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensure a dedicated "system" user exists for internal operations.
 */
@Component
@RequiredArgsConstructor
public class SystemUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("system").orElseGet(() -> {
            User system = new User();
            system.setUsername("system");
            system.setEmail("system@openisle.local");
            system.setPassword(passwordEncoder.encode("system"));
            system.setRole(Role.ADMIN);
            system.setVerified(true);
            system.setApproved(true);
            return userRepository.save(system);
        });
    }
}

