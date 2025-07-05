package com.openisle.controller;

import com.openisle.model.Category;
import com.openisle.service.CategoryService;
import com.openisle.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final PostService postService;

    @PostMapping
    public CategoryDto create(@RequestBody CategoryRequest req) {
        Category c = categoryService.createCategory(req.getName(), req.getDescribe(), req.getIcon(), req.getSmallIcon());
        return toDto(c);
    }

    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        Category c = categoryService.updateCategory(id, req.getName(), req.getDescribe(), req.getIcon(), req.getSmallIcon());
        return toDto(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.listCategories().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDto get(@PathVariable Long id) {
        return toDto(categoryService.getCategory(id));
    }

    @GetMapping("/{id}/posts")
    public List<PostSummaryDto> listPostsByCategory(@PathVariable Long id,
                                                    @RequestParam(value = "page", required = false) Integer page,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return postService.listPostsByCategories(java.util.List.of(id), page, pageSize)
                .stream()
                .map(p -> {
                    PostSummaryDto dto = new PostSummaryDto();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setIcon(c.getIcon());
        dto.setSmallIcon(c.getSmallIcon());
        dto.setDescribe(c.getDescribe());
        return dto;
    }

    @Data
    private static class CategoryRequest {
        private String name;
        private String describe;
        private String icon;
        private String smallIcon;
    }

    @Data
    private static class CategoryDto {
        private Long id;
        private String name;
        private String describe;
        private String icon;
        private String smallIcon;
    }

    @Data
    private static class PostSummaryDto {
        private Long id;
        private String title;
    }
}
