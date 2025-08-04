package com.openisle.dto;

import lombok.Data;

import java.util.List;

/**
 * Request body for creating or updating a post.
 */
@Data
public class PostRequest {
    private Long categoryId;
    private String title;
    private String content;
    private List<Long> tagIds;
    private String captcha;
}

