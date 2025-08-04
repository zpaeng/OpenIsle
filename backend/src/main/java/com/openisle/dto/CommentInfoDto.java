package com.openisle.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** DTO for comment information in user profiles. */
@Data
public class CommentInfoDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private PostMetaDto post;
    private ParentCommentDto parentComment;
}
