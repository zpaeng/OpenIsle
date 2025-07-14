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
        return notificationRepository.save(n);
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
