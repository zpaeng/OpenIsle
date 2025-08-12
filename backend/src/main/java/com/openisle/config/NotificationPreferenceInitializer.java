package com.openisle.config;

import com.openisle.model.NotificationType;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Ensure default notification preferences are applied to existing users.
 */
@Component
@RequiredArgsConstructor
public class NotificationPreferenceInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Set<NotificationType> disabled = user.getDisabledNotificationTypes();
            boolean changed = false;
            if (disabled.add(NotificationType.POST_VIEWED)) {
                changed = true;
            }
            if (disabled.add(NotificationType.USER_ACTIVITY)) {
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
            }
        }
    }
}
