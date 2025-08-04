package com.openisle.dto;

import lombok.Data;

import java.util.List;

/** Request body for saving a draft. */
@Data
public class DraftRequest {
    private String title;
    private String content;
    private Long categoryId;
    private List<Long> tagIds;
}
