package com.openisle.controller;

import com.openisle.model.Tag;
import com.openisle.service.TagService;
import com.openisle.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final PostService postService;

    @PostMapping
    public TagDto create(@RequestBody TagRequest req) {
        Tag tag = tagService.createTag(req.getName(), req.getDescribe(), req.getIcon());
        return toDto(tag);
    }

    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @RequestBody TagRequest req) {
        Tag tag = tagService.updateTag(id, req.getName(), req.getDescribe(), req.getIcon());
        return toDto(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.deleteTag(id);
    }

    @GetMapping
    public List<TagDto> list() {
        return tagService.listTags().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagDto get(@PathVariable Long id) {
        return toDto(tagService.getTag(id));
    }

    @GetMapping("/{id}/posts")
    public List<PostSummaryDto> listPostsByTag(@PathVariable Long id,
                                               @RequestParam(value = "page", required = false) Integer page,
                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return postService.listPostsByTags(java.util.List.of(id), page, pageSize)
                .stream()
                .map(p -> {
                    PostSummaryDto dto = new PostSummaryDto();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private TagDto toDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setIcon(tag.getIcon());
        dto.setDescribe(tag.getDescribe());
        return dto;
    }

    @Data
    private static class TagRequest {
        private String name;
        private String describe;
        private String icon;
    }

    @Data
    private static class TagDto {
        private Long id;
        private String name;
        private String describe;
        private String icon;
    }

    @Data
    private static class PostSummaryDto {
        private Long id;
        private String title;
    }
}
