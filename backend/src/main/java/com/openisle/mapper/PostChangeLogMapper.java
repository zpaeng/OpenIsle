package com.openisle.mapper;

import com.openisle.dto.PostChangeLogDto;
import com.openisle.model.*;
import org.springframework.stereotype.Component;

@Component
public class PostChangeLogMapper {
    public PostChangeLogDto toDto(PostChangeLog log) {
        PostChangeLogDto dto = new PostChangeLogDto();
        dto.setId(log.getId());
        dto.setUsername(log.getUser().getUsername());
        dto.setType(log.getType());
        dto.setTime(log.getCreatedAt());
        if (log instanceof PostTitleChangeLog t) {
            dto.setOldTitle(t.getOldTitle());
            dto.setNewTitle(t.getNewTitle());
        } else if (log instanceof PostContentChangeLog c) {
            dto.setOldContent(c.getOldContent());
            dto.setNewContent(c.getNewContent());
        } else if (log instanceof PostCategoryChangeLog cat) {
            dto.setOldCategory(cat.getOldCategory());
            dto.setNewCategory(cat.getNewCategory());
        } else if (log instanceof PostTagChangeLog tag) {
            dto.setOldTags(tag.getOldTags());
            dto.setNewTags(tag.getNewTags());
        } else if (log instanceof PostClosedChangeLog cl) {
            dto.setOldClosed(cl.isOldClosed());
            dto.setNewClosed(cl.isNewClosed());
        } else if (log instanceof PostPinnedChangeLog p) {
            dto.setOldPinnedAt(p.getOldPinnedAt());
            dto.setNewPinnedAt(p.getNewPinnedAt());
        } else if (log instanceof PostFeaturedChangeLog f) {
            dto.setOldFeatured(f.isOldFeatured());
            dto.setNewFeatured(f.isNewFeatured());
        }
        return dto;
    }
}
