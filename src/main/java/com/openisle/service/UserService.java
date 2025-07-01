package com.openisle.service;

import com.openisle.model.User;
import com.openisle.model.Role;
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
        // ── 先按用户名查 ──────────────────────────────────────────
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            User u = byUsername.get();
            if (u.isVerified()) {                           // 已验证 → 直接拒绝
                throw new IllegalStateException("User name already exists");
            }
            // 未验证 → 允许“重注册”：覆盖必要字段并重新发验证码
            u.setEmail(email);                              // 若不允许改邮箱可去掉
            u.setPassword(passwordEncoder.encode(password));
            u.setVerificationCode(genCode());
            return userRepository.save(u);
        }

        // ── 再按邮箱查 ───────────────────────────────────────────
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User u = byEmail.get();
            if (u.isVerified()) {                           // 已验证 → 直接拒绝
                throw new IllegalStateException("User email already exists");
            }
            // 未验证 → 允许“重注册”
            u.setUsername(username);                        // 若不允许改用户名可去掉
            u.setPassword(passwordEncoder.encode(password));
            u.setVerificationCode(genCode());
            return userRepository.save(u);
        }

        // ── 完全新用户 ───────────────────────────────────────────
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setVerified(false);
        user.setVerificationCode(genCode());
        return userRepository.save(user);
    }

    private String genCode() {
        return String.format("%06d", new Random().nextInt(1000000));
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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateAvatar(String username, String avatarUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAvatar(avatarUrl);
        return userRepository.save(user);
    }
}
