package com.openisle.controller;

import com.openisle.dto.TagDto;
import com.openisle.mapper.TagMapper;
import com.openisle.model.Tag;
import com.openisle.service.PostService;
import com.openisle.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {
    private final TagService tagService;
    private final PostService postService;
    private final TagMapper tagMapper;

    @GetMapping("/pending")
    public List<TagDto> pendingTags() {
        return tagService.listPendingTags().stream()
                .map(t -> tagMapper.toDto(t, postService.countPostsByTag(t.getId())))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/approve")
    public TagDto approve(@PathVariable Long id) {
        Tag tag = tagService.approveTag(id);
        long count = postService.countPostsByTag(tag.getId());
        return tagMapper.toDto(tag, count);
    }
}
