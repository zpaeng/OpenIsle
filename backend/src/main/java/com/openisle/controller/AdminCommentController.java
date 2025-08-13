package com.openisle.controller;

import com.openisle.dto.CommentDto;
import com.openisle.mapper.CommentMapper;
import com.openisle.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints for administrators to manage comments.
 */
@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/{id}/pin")
    public CommentDto pin(@PathVariable Long id, Authentication auth) {
        return commentMapper.toDto(commentService.pinComment(auth.getName(), id));
    }

    @PostMapping("/{id}/unpin")
    public CommentDto unpin(@PathVariable Long id, Authentication auth) {
        return commentMapper.toDto(commentService.unpinComment(auth.getName(), id));
    }
}
