package com.openisle.service;

import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(false);
        String code = String.format("%06d", new Random().nextInt(1000000));
        user.setVerificationCode(code);
        return userRepository.save(user);
    }

    public boolean verifyCode(String username, String code) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && code.equals(userOpt.get().getVerificationCode())) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(User::isVerified)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}
