package com.openisle.dto;

import com.openisle.model.PostChangeType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostChangeLogDto {
    private Long id;
    private String username;
    private String userAvatar;
    private PostChangeType type;
    private LocalDateTime time;
    private String oldTitle;
    private String newTitle;
    private String oldContent;
    private String newContent;
    private String oldCategory;
    private String newCategory;
    private String oldTags;
    private String newTags;
    private Boolean oldClosed;
    private Boolean newClosed;
    private LocalDateTime oldPinnedAt;
    private LocalDateTime newPinnedAt;
    private Boolean oldFeatured;
    private Boolean newFeatured;
}
