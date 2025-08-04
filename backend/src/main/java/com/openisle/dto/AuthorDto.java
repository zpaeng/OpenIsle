package com.openisle.dto;

import lombok.Data;

/**
 * DTO representing a post or comment author.
 */
@Data
public class AuthorDto {
    private Long id;
    private String username;
    private String avatar;
}

