package com.openisle.controller;

import com.openisle.model.Comment;
import com.openisle.model.Post;
import com.openisle.model.Reaction;
import com.openisle.service.CommentService;
import com.openisle.service.PostService;
import com.openisle.service.ReactionService;
import com.openisle.service.CaptchaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final CaptchaService captchaService;

    @Value("${app.captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${app.captcha.post-enabled:false}")
    private boolean postCaptchaEnabled;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequest req, Authentication auth) {
        if (captchaEnabled && postCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().build();
        }
        Post post = postService.createPost(auth.getName(), req.getCategoryId(),
                req.getTitle(), req.getContent(), req.getTagIds());
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping
    public List<PostDto> listPosts(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<Long> ids = categoryIds;
        if (categoryId != null) {
            ids = java.util.List.of(categoryId);
        }
        return postService.listPostsByCategories(ids, page, pageSize)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(post.getAuthor().getUsername());
        dto.setCategory(post.getCategory().getName());
        dto.setTags(post.getTags().stream().map(com.openisle.model.Tag::getName).collect(Collectors.toList()));
        dto.setViews(post.getViews());

        List<ReactionDto> reactions = reactionService.getReactionsForPost(post.getId())
                .stream()
                .map(this::toReactionDto)
                .collect(Collectors.toList());
        dto.setReactions(reactions);

        List<CommentDto> comments = commentService.getCommentsForPost(post.getId())
                .stream()
                .map(this::toCommentDtoWithReplies)
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    private CommentDto toCommentDtoWithReplies(Comment comment) {
        CommentDto dto = toCommentDto(comment);
        List<CommentDto> replies = commentService.getReplies(comment.getId()).stream()
                .map(this::toCommentDtoWithReplies)
                .collect(Collectors.toList());
        dto.setReplies(replies);

        List<ReactionDto> reactions = reactionService.getReactionsForComment(comment.getId())
                .stream()
                .map(this::toReactionDto)
                .collect(Collectors.toList());
        dto.setReactions(reactions);

        return dto;
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthor(comment.getAuthor().getUsername());
        return dto;
    }

    private ReactionDto toReactionDto(Reaction reaction) {
        ReactionDto dto = new ReactionDto();
        dto.setId(reaction.getId());
        dto.setType(reaction.getType());
        dto.setUser(reaction.getUser().getUsername());
        if (reaction.getPost() != null) {
            dto.setPostId(reaction.getPost().getId());
        }
        if (reaction.getComment() != null) {
            dto.setCommentId(reaction.getComment().getId());
        }
        return dto;
    }

    @Data
    private static class PostRequest {
        private Long categoryId;
        private String title;
        private String content;
        private java.util.List<Long> tagIds;
        private String captcha;
    }

    @Data
    private static class PostDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private String author;
        private String category;
        private java.util.List<String> tags;
        private long views;
        private List<CommentDto> comments;
        private List<ReactionDto> reactions;
    }

    @Data
    private static class CommentDto {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private String author;
        private List<CommentDto> replies;
        private List<ReactionDto> reactions;
    }

    @Data
    private static class ReactionDto {
        private Long id;
        private com.openisle.model.ReactionType type;
        private String user;
        private Long postId;
        private Long commentId;
    }
}
