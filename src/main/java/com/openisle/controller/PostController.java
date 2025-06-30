package com.openisle.controller;

import com.openisle.model.Comment;
import com.openisle.model.Post;
import com.openisle.model.Reaction;
import com.openisle.service.CommentService;
import com.openisle.service.PostService;
import com.openisle.service.ReactionService;
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
    private final CommentService commentService;
    private final ReactionService reactionService;

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

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(post.getAuthor().getUsername());
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
