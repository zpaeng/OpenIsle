package com.openisle.mapper;

import com.openisle.dto.CategoryDto;
import com.openisle.dto.PostChangeLogDto;
import com.openisle.dto.TagDto;
import com.openisle.model.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostChangeLogMapper {
    public PostChangeLogDto toDto(PostChangeLog log) {
        PostChangeLogDto dto = new PostChangeLogDto();
        dto.setId(log.getId());
        if (log.getUser() != null) {
            dto.setUsername(log.getUser().getUsername());
            dto.setUserAvatar(log.getUser().getAvatar());
        }
        dto.setType(log.getType());
        dto.setTime(log.getCreatedAt());
        if (log instanceof PostTitleChangeLog t) {
            dto.setOldTitle(t.getOldTitle());
            dto.setNewTitle(t.getNewTitle());
        } else if (log instanceof PostContentChangeLog c) {
            dto.setOldContent(c.getOldContent());
            dto.setNewContent(c.getNewContent());
        } else if (log instanceof PostCategoryChangeLog cat) {
            if (cat.getOldCategory() != null) {
                CategoryDto oldCat = new CategoryDto();
                oldCat.setName(cat.getOldCategory());
                dto.setOldCategory(oldCat);
            }
            if (cat.getNewCategory() != null) {
                CategoryDto newCat = new CategoryDto();
                newCat.setName(cat.getNewCategory());
                dto.setNewCategory(newCat);
            }
        } else if (log instanceof PostTagChangeLog tag) {
            if (tag.getOldTags() != null && !tag.getOldTags().isBlank()) {
                List<TagDto> oldTags = Arrays.stream(tag.getOldTags().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(name -> {
                            TagDto t = new TagDto();
                            t.setName(name);
                            return t;
                        })
                        .collect(Collectors.toList());
                dto.setOldTags(oldTags);
            }
            if (tag.getNewTags() != null && !tag.getNewTags().isBlank()) {
                List<TagDto> newTags = Arrays.stream(tag.getNewTags().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(name -> {
                            TagDto t = new TagDto();
                            t.setName(name);
                            return t;
                        })
                        .collect(Collectors.toList());
                dto.setNewTags(newTags);
            }
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
