package com.openisle.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a comment and its nested replies.
 */
@Data
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime pinnedAt;
    private AuthorDto author;
    private List<CommentDto> replies;
    private List<ReactionDto> reactions;
    private int reward;
    private int pointReward;
}

