package com.openisle.mapper;

import com.openisle.dto.*;
import com.openisle.model.*;
import com.openisle.service.CommentService;
import com.openisle.service.ReactionService;
import com.openisle.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsible for converting domain models into DTOs.
 */
@Component
@RequiredArgsConstructor
public class PostMapper {

    private final CommentService commentService;
    private final ReactionService reactionService;
    private final SubscriptionService subscriptionService;

    public PostSummaryDto toSummaryDto(Post post) {
        PostSummaryDto dto = new PostSummaryDto();
        applyCommon(post, dto);
        return dto;
    }

    public PostDetailDto toDetailDto(Post post, String viewer) {
        PostDetailDto dto = new PostDetailDto();
        applyCommon(post, dto);
        List<CommentDto> comments = commentService.getCommentsForPost(post.getId(), CommentSort.OLDEST)
                .stream()
                .map(this::toCommentDtoWithReplies)
                .collect(Collectors.toList());
        dto.setComments(comments);
        dto.setSubscribed(viewer != null && subscriptionService.isPostSubscribed(viewer, post.getId()));
        return dto;
    }

    private void applyCommon(Post post, PostSummaryDto dto) {
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(toAuthorDto(post.getAuthor()));
        dto.setCategory(toCategoryDto(post.getCategory()));
        dto.setTags(post.getTags().stream().map(this::toTagDto).collect(Collectors.toList()));
        dto.setViews(post.getViews());
        dto.setStatus(post.getStatus());
        dto.setPinnedAt(post.getPinnedAt());

        List<ReactionDto> reactions = reactionService.getReactionsForPost(post.getId())
                .stream()
                .map(this::toReactionDto)
                .collect(Collectors.toList());
        dto.setReactions(reactions);

        List<User> participants = commentService.getParticipants(post.getId(), 5);
        dto.setParticipants(participants.stream().map(this::toAuthorDto).collect(Collectors.toList()));

        LocalDateTime last = commentService.getLastCommentTime(post.getId());
        dto.setLastReplyAt(last != null ? last : post.getCreatedAt());
        dto.setReward(0);
        dto.setSubscribed(false);
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
        dto.setAuthor(toAuthorDto(comment.getAuthor()));
        dto.setReward(0);
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
        dto.setReward(0);
        return dto;
    }

    private CategoryDto toCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setSmallIcon(category.getSmallIcon());
        return dto;
    }

    private TagDto toTagDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        dto.setIcon(tag.getIcon());
        dto.setSmallIcon(tag.getSmallIcon());
        dto.setCreatedAt(tag.getCreatedAt());
        return dto;
    }

    private AuthorDto toAuthorDto(User user) {
        AuthorDto dto = new AuthorDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}

