package com.openisle.controller;

import com.openisle.dto.*;
import com.openisle.exception.NotFoundException;
import com.openisle.mapper.TagMapper;
import com.openisle.mapper.UserMapper;
import com.openisle.model.User;
import com.openisle.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final ReactionService reactionService;
    private final TagService tagService;
    private final SubscriptionService subscriptionService;
    private final LevelService levelService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final TagMapper tagMapper;

    @Value("${app.upload.check-type:true}")
    private boolean checkImageType;

    @Value("${app.upload.max-size:5242880}")
    private long maxUploadSize;

    @Value("${app.user.posts-limit:10}")
    private int defaultPostsLimit;

    @Value("${app.user.replies-limit:50}")
    private int defaultRepliesLimit;

    @Value("${app.user.tags-limit:50}")
    private int defaultTagsLimit;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(userMapper.toDto(user, auth));
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

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto,
                                           Authentication auth) {
        User user = userService.updateProfile(auth.getName(), dto.getUsername(), dto.getIntroduction());
        return ResponseEntity.ok(Map.of(
                "token", jwtService.generateToken(user.getUsername()),
                "user", userMapper.toDto(user, auth)
        ));
    }

    @PostMapping("/me/signin")
    public Map<String, Integer> signIn(Authentication auth) {
        int reward = levelService.awardForSignin(auth.getName());
        return Map.of("reward", reward);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<UserDto> getUser(@PathVariable("identifier") String identifier,
                                           Authentication auth) {
        User user = userService.findByIdentifier(identifier).orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseEntity.ok(userMapper.toDto(user, auth));
    }

    @GetMapping("/{identifier}/posts")
    public java.util.List<PostMetaDto> userPosts(@PathVariable("identifier") String identifier,
                                                 @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultPostsLimit;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return postService.getRecentPostsByUser(user.getUsername(), l).stream()
                .map(userMapper::toMetaDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/subscribed-posts")
    public java.util.List<PostMetaDto> subscribedPosts(@PathVariable("identifier") String identifier,
                                                       @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultPostsLimit;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return subscriptionService.getSubscribedPosts(user.getUsername()).stream()
                .limit(l)
                .map(userMapper::toMetaDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/replies")
    public java.util.List<CommentInfoDto> userReplies(@PathVariable("identifier") String identifier,
                                                      @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultRepliesLimit;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return commentService.getRecentCommentsByUser(user.getUsername(), l).stream()
                .map(userMapper::toCommentInfoDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/hot-posts")
    public java.util.List<PostMetaDto> hotPosts(@PathVariable("identifier") String identifier,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : 10;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        java.util.List<Long> ids = reactionService.topPostIds(user.getUsername(), l);
        return postService.getPostsByIds(ids).stream()
                .map(userMapper::toMetaDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/hot-replies")
    public java.util.List<CommentInfoDto> hotReplies(@PathVariable("identifier") String identifier,
                                                     @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : 10;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        java.util.List<Long> ids = reactionService.topCommentIds(user.getUsername(), l);
        return commentService.getCommentsByIds(ids).stream()
                .map(userMapper::toCommentInfoDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/hot-tags")
    public java.util.List<TagDto> hotTags(@PathVariable("identifier") String identifier,
                                          @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : 10;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return tagService.getTagsByUser(user.getUsername()).stream()
                .map(t -> tagMapper.toDto(t, postService.countPostsByTag(t.getId())))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(l)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/tags")
    public java.util.List<TagDto> userTags(@PathVariable("identifier") String identifier,
                                           @RequestParam(value = "limit", required = false) Integer limit) {
        int l = limit != null ? limit : defaultTagsLimit;
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return tagService.getRecentTagsByUser(user.getUsername(), l).stream()
                .map(t -> tagMapper.toDto(t, postService.countPostsByTag(t.getId())))
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/following")
    public java.util.List<UserDto> following(@PathVariable("identifier") String identifier) {
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return subscriptionService.getSubscribedUsers(user.getUsername()).stream()
                .map(userMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/followers")
    public java.util.List<UserDto> followers(@PathVariable("identifier") String identifier) {
        User user = userService.findByIdentifier(identifier).orElseThrow();
        return subscriptionService.getSubscribers(user.getUsername()).stream()
                .map(userMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/admins")
    public java.util.List<UserDto> admins() {
        return userService.getAdmins().stream()
                .map(userMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{identifier}/all")
    public ResponseEntity<UserAggregateDto> userAggregate(@PathVariable("identifier") String identifier,
                                                          @RequestParam(value = "postsLimit", required = false) Integer postsLimit,
                                                          @RequestParam(value = "repliesLimit", required = false) Integer repliesLimit,
                                                          Authentication auth) {
        User user = userService.findByIdentifier(identifier).orElseThrow();
        int pLimit = postsLimit != null ? postsLimit : defaultPostsLimit;
        int rLimit = repliesLimit != null ? repliesLimit : defaultRepliesLimit;
        java.util.List<PostMetaDto> posts = postService.getRecentPostsByUser(user.getUsername(), pLimit).stream()
                .map(userMapper::toMetaDto)
                .collect(java.util.stream.Collectors.toList());
        java.util.List<CommentInfoDto> replies = commentService.getRecentCommentsByUser(user.getUsername(), rLimit).stream()
                .map(userMapper::toCommentInfoDto)
                .collect(java.util.stream.Collectors.toList());
        UserAggregateDto dto = new UserAggregateDto();
        dto.setUser(userMapper.toDto(user, auth));
        dto.setPosts(posts);
        dto.setReplies(replies);
        return ResponseEntity.ok(dto);
    }
}
