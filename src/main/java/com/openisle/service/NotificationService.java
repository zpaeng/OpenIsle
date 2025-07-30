package com.openisle.service;

import com.openisle.model.*;
import com.openisle.repository.NotificationRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Service for creating and retrieving notifications. */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    public Notification createNotification(User user, NotificationType type, Post post, Comment comment, Boolean approved) {
        return createNotification(user, type, post, comment, approved, null, null, null);
    }

    public Notification createNotification(User user, NotificationType type, Post post, Comment comment, Boolean approved,
                                           User fromUser, ReactionType reactionType, String content) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setPost(post);
        n.setComment(comment);
        n.setApproved(approved);
        n.setFromUser(fromUser);
        n.setReactionType(reactionType);
        n.setContent(content);
        Notification saved = notificationRepository.save(n);
        pushNotificationService.sendNotification(user, "You have a new notification");
        return saved;
    }

    /**
     * Create notifications for all admins when a user submits a register request.
     * Old register request notifications from the same applicant are removed first.
     */
    @org.springframework.transaction.annotation.Transactional
    public void createRegisterRequestNotifications(User applicant, String reason) {
        notificationRepository.deleteByTypeAndFromUser(NotificationType.REGISTER_REQUEST, applicant);
        for (User admin : userRepository.findByRole(Role.ADMIN)) {
            createNotification(admin, NotificationType.REGISTER_REQUEST, null, null,
                    null, applicant, null, reason);
        }
    }

    /**
     * Create notifications for all admins when a user redeems an activity.
     * Old redeem notifications from the same user are removed first.
     */
    @org.springframework.transaction.annotation.Transactional
    public void createActivityRedeemNotifications(User user, String content) {
        notificationRepository.deleteByTypeAndFromUser(NotificationType.ACTIVITY_REDEEM, user);
        for (User admin : userRepository.findByRole(Role.ADMIN)) {
            createNotification(admin, NotificationType.ACTIVITY_REDEEM, null, null,
                    null, user, null, content);
        }
    }

    public List<Notification> listNotifications(String username, Boolean read) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.openisle.exception.NotFoundException("User not found"));
        if (read == null) {
            return notificationRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return notificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, read);
    }

    public void markRead(String username, List<Long> ids) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.openisle.exception.NotFoundException("User not found"));
        List<Notification> notifs = notificationRepository.findAllById(ids);
        for (Notification n : notifs) {
            if (n.getUser().getId().equals(user.getId())) {
                n.setRead(true);
            }
        }
        notificationRepository.saveAll(notifs);
    }

    public long countUnread(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.openisle.exception.NotFoundException("User not found"));
        return notificationRepository.countByUserAndRead(user, false);
    }
}
