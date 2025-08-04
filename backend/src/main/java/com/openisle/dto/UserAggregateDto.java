package com.openisle.dto;

import lombok.Data;

import java.util.List;

/** Aggregated user data including posts and replies. */
@Data
public class UserAggregateDto {
    private UserDto user;
    private List<PostMetaDto> posts;
    private List<CommentInfoDto> replies;
}
