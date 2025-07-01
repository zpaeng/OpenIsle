package com.openisle.controller;

import com.openisle.model.Comment;
import com.openisle.service.CommentService;
import com.openisle.service.CaptchaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CaptchaService captchaService;

    @Value("${app.captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${app.captcha.comment-enabled:false}")
    private boolean commentCaptchaEnabled;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody CommentRequest req,
                                                    Authentication auth) {
        if (captchaEnabled && commentCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().build();
        }
        Comment comment = commentService.addComment(auth.getName(), postId, req.getContent());
        return ResponseEntity.ok(toDto(comment));
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentDto> replyComment(@PathVariable Long commentId,
                                                   @RequestBody CommentRequest req,
                                                   Authentication auth) {
        if (captchaEnabled && commentCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().build();
        }
        Comment comment = commentService.addReply(auth.getName(), commentId, req.getContent());
        return ResponseEntity.ok(toDto(comment));
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> listComments(@PathVariable Long postId) {
        return commentService.getCommentsForPost(postId).stream()
                .map(this::toDtoWithReplies)
                .collect(Collectors.toList());
    }

    private CommentDto toDtoWithReplies(Comment comment) {
        CommentDto dto = toDto(comment);
        List<CommentDto> replies = commentService.getReplies(comment.getId()).stream()
                .map(this::toDtoWithReplies)
                .collect(Collectors.toList());
        dto.setReplies(replies);
        return dto;
    }

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthor(comment.getAuthor().getUsername());
        return dto;
    }

    @Data
    private static class CommentRequest {
        private String content;
        private String captcha;
    }

    @Data
    private static class CommentDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private String author;
        private List<CommentDto> replies;
    }
}
