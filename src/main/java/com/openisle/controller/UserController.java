package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.ImageUploader;
import com.openisle.service.UserService;
import com.openisle.service.PostService;
import com.openisle.service.CommentService;
import org.springframework.beans.factory.annotation.Value;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ImageUploader imageUploader;
    private final PostService postService;
    private final CommentService commentService;

    @Value("${app.upload.check-type:true}")
    private boolean checkImageType;

    @Value("${app.upload.max-size:5242880}")
    private long maxUploadSize;

    @Value("${app.user.posts-limit:10}")
    private int defaultPostsLimit;

    @Value("${app.user.replies-limit:50}")
    private int defaultRepliesLimit;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(toDto(user));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          Authentication auth) {
        if (checkImageType && (file.getContentType() == null || !file.getContentType().startsWith("image/"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is not an image"));
        }
        if (file.getSize() > maxUploadSize) {
            return ResponseEntity.badRequest().body(Map.of("error", "File too large"));
        }
        String url = null;
        try {
            url = imageUploader.upload(file.getBytes(), file.getOriginalFilename()).join();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("url", url));
        }
        userService.updateAvatar(auth.getName(), url);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(toDto(user));
    }

    @GetMapping("/{username}/posts")
    public java.util.List<PostMetaDto> userPosts(@PathVariable String username,
                                                 @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultPostsLimit;
        return postService.getRecentPostsByUser(username, l).stream()
                .map(this::toMetaDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{username}/replies")
    public java.util.List<CommentInfoDto> userReplies(@PathVariable String username,
                                                      @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultRepliesLimit;
        return commentService.getRecentCommentsByUser(username, l).stream()
                .map(this::toCommentInfoDto)
                .collect(java.util.stream.Collectors.toList());
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    private PostMetaDto toMetaDto(com.openisle.model.Post post) {
        PostMetaDto dto = new PostMetaDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setCategory(post.getCategory().getName());
        dto.setViews(post.getViews());
        return dto;
    }

    private CommentInfoDto toCommentInfoDto(com.openisle.model.Comment comment) {
        CommentInfoDto dto = new CommentInfoDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setPostId(comment.getPost().getId());
        return dto;
    }

    @Data
    private static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String avatar;
    }

    @Data
    private static class PostMetaDto {
        private Long id;
        private String title;
        private java.time.LocalDateTime createdAt;
        private String category;
        private long views;
    }

    @Data
    private static class CommentInfoDto {
        private Long id;
        private String content;
        private java.time.LocalDateTime createdAt;
        private Long postId;
    }
}
