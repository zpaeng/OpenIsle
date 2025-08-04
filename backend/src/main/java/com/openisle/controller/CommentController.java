package com.openisle.controller;

import com.openisle.model.Comment;
import com.openisle.dto.CommentDto;
import com.openisle.dto.CommentRequest;
import com.openisle.mapper.CommentMapper;
import com.openisle.service.CaptchaService;
import com.openisle.service.CommentService;
import com.openisle.service.LevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final LevelService levelService;
    private final CaptchaService captchaService;
    private final CommentMapper commentMapper;

    @Value("${app.captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${app.captcha.comment-enabled:false}")
    private boolean commentCaptchaEnabled;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody CommentRequest req,
                                                    Authentication auth) {
        log.debug("createComment called by user {} for post {}", auth.getName(), postId);
        if (captchaEnabled && commentCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            log.debug("Captcha verification failed for user {} on post {}", auth.getName(), postId);
            return ResponseEntity.badRequest().build();
        }
        Comment comment = commentService.addComment(auth.getName(), postId, req.getContent());
        CommentDto dto = commentMapper.toDto(comment);
        dto.setReward(levelService.awardForComment(auth.getName()));
        log.debug("createComment succeeded for comment {}", comment.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentDto> replyComment(@PathVariable Long commentId,
                                                   @RequestBody CommentRequest req,
                                                   Authentication auth) {
        log.debug("replyComment called by user {} for comment {}", auth.getName(), commentId);
        if (captchaEnabled && commentCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            log.debug("Captcha verification failed for user {} on comment {}", auth.getName(), commentId);
            return ResponseEntity.badRequest().build();
        }
        Comment comment = commentService.addReply(auth.getName(), commentId, req.getContent());
        CommentDto dto = commentMapper.toDto(comment);
        dto.setReward(levelService.awardForComment(auth.getName()));
        log.debug("replyComment succeeded for comment {}", comment.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> listComments(@PathVariable Long postId,
                                         @RequestParam(value = "sort", required = false, defaultValue = "OLDEST") com.openisle.model.CommentSort sort) {
        log.debug("listComments called for post {} with sort {}", postId, sort);
        List<CommentDto> list = commentService.getCommentsForPost(postId, sort).stream()
                .map(commentMapper::toDtoWithReplies)
                .collect(Collectors.toList());
        log.debug("listComments returning {} comments", list.size());
        return list;
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable Long id, Authentication auth) {
        log.debug("deleteComment called by user {} for comment {}", auth.getName(), id);
        commentService.deleteComment(auth.getName(), id);
        log.debug("deleteComment completed for comment {}", id);
    }
}
