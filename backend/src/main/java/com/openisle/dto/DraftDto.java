package com.openisle.dto;

import lombok.Data;

import java.util.List;

/** DTO representing a saved draft. */
@Data
public class DraftDto {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private List<Long> tagIds;
}
