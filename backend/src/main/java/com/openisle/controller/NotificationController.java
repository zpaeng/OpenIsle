package com.openisle.controller;

import com.openisle.dto.AuthorDto;
import com.openisle.dto.CommentDto;
import com.openisle.dto.NotificationDto;
import com.openisle.dto.NotificationMarkReadRequest;
import com.openisle.dto.NotificationUnreadCountDto;
import com.openisle.dto.PostSummaryDto;
import com.openisle.model.Comment;
import com.openisle.model.Notification;
import com.openisle.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Endpoints for user notifications. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> list(@RequestParam(value = "read", required = false) Boolean read,
                                      Authentication auth) {
        return notificationService.listNotifications(auth.getName(), read).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread-count")
    public NotificationUnreadCountDto unreadCount(Authentication auth) {
        long count = notificationService.countUnread(auth.getName());
        NotificationUnreadCountDto uc = new NotificationUnreadCountDto();
        uc.setCount(count);
        return uc;
    }

    @PostMapping("/read")
    public void markRead(@RequestBody NotificationMarkReadRequest req, Authentication auth) {
        notificationService.markRead(auth.getName(), req.getIds());
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        if (n.getPost() != null) {
            PostSummaryDto postDto = new PostSummaryDto();
            postDto.setId(n.getPost().getId());
            postDto.setTitle(n.getPost().getTitle());
            dto.setPost(postDto);
        }
        if (n.getComment() != null) {
            dto.setComment(toCommentDto(n.getComment()));
            Comment parent = n.getComment().getParent();
            if (parent != null) {
                dto.setParentComment(toCommentDto(parent));
            }
        }
        if (n.getFromUser() != null) {
            AuthorDto author = new AuthorDto();
            author.setId(n.getFromUser().getId());
            author.setUsername(n.getFromUser().getUsername());
            author.setAvatar(n.getFromUser().getAvatar());
            dto.setFromUser(author);
        }
        if (n.getReactionType() != null) {
            dto.setReactionType(n.getReactionType());
        }
        dto.setApproved(n.getApproved());
        dto.setContent(n.getContent());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        AuthorDto author = new AuthorDto();
        author.setId(comment.getAuthor().getId());
        author.setUsername(comment.getAuthor().getUsername());
        author.setAvatar(comment.getAuthor().getAvatar());
        dto.setAuthor(author);
        return dto;
    }
}
