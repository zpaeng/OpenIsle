package com.openisle.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Detailed DTO for a post, including comments.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostDetailDto extends PostSummaryDto {
    private List<CommentDto> comments;
}

