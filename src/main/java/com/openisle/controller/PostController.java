package com.openisle.controller;

import com.openisle.model.Post;
import com.openisle.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
curl -X POST http://localhost:8080/api/posts \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X3VzZXIxIiwiaWF0IjoxNzUxMjg0OTU2LCJleHAiOjE3NTEzNzEzNTZ9.u84elcDTK2gIvuS4dKJCdE21pRSgY265fvdm9m9DnCQ" \
    -d '{ "title": "First", "content": "Post" }'

curl http://localhost:8080/api/posts \
    -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X3VzZXIxIiwiaWF0IjoxNzUxMjg0OTU2LCJleHAiOjE3NTEzNzEzNTZ9.u84elcDTK2gIvuS4dKJCdE21pRSgY265fvdm9m9DnCQ"

curl http://localhost:8080/api/posts/1 \
    -H "Authorization: Bearer <token>"
 */

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequest req, Authentication auth) {
        Post post = postService.createPost(auth.getName(), req.getTitle(), req.getContent());
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping
    public List<PostDto> listPosts() {
        return postService.listPosts().stream().map(this::toDto).collect(Collectors.toList());
    }

    // todo(tim): 希望返回文章所有评论，包含所有评论的子评论（包含reaction）、文章本身的reaction
    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(post.getAuthor().getUsername());
        dto.setViews(post.getViews());
        return dto;
    }

    @Data
    private static class PostRequest {
        private String title;
        private String content;
    }

    @Data
    private static class PostDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private String author;
        private long views;
    }
}
