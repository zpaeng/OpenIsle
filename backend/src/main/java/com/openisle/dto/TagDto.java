package com.openisle.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing a tag.
 */
@Data
public class TagDto {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String smallIcon;
    private LocalDateTime createdAt;
    private Long count;
}

