package com.openisle.controller;

import com.openisle.dto.CategoryDto;
import com.openisle.dto.CategoryRequest;
import com.openisle.dto.PostSummaryDto;
import com.openisle.mapper.PostMapper;
import com.openisle.model.Category;
import com.openisle.service.CategoryService;
import com.openisle.service.PostService;
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
    private final PostMapper postMapper;

    @PostMapping
    public CategoryDto create(@RequestBody CategoryRequest req) {
        Category c = categoryService.createCategory(req.getName(), req.getDescription(), req.getIcon(), req.getSmallIcon());
        long count = postService.countPostsByCategory(c.getId());
        return toDto(c, count);
    }

    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        Category c = categoryService.updateCategory(id, req.getName(), req.getDescription(), req.getIcon(), req.getSmallIcon());
        long count = postService.countPostsByCategory(c.getId());
        return toDto(c, count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.listCategories().stream()
                .map(c -> toDto(c, postService.countPostsByCategory(c.getId())))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDto get(@PathVariable Long id) {
        Category c = categoryService.getCategory(id);
        long count = postService.countPostsByCategory(c.getId());
        return toDto(c, count);
    }

    @GetMapping("/{id}/posts")
    public List<PostSummaryDto> listPostsByCategory(@PathVariable Long id,
                                                    @RequestParam(value = "page", required = false) Integer page,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return postService.listPostsByCategories(java.util.List.of(id), page, pageSize)
                .stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    private CategoryDto toDto(Category c, long count) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setIcon(c.getIcon());
        dto.setSmallIcon(c.getSmallIcon());
        dto.setDescription(c.getDescription());
        dto.setCount(count);
        return dto;
    }
}
