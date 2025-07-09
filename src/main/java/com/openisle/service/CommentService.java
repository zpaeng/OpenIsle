package com.openisle.service;

import com.openisle.model.Comment;
import com.openisle.model.Post;
import com.openisle.model.User;
import com.openisle.model.NotificationType;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import com.openisle.service.NotificationService;
import com.openisle.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;

    public Comment addComment(String username, Long postId, String content) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent(content);
        comment = commentRepository.save(comment);
        if (!author.getId().equals(post.getAuthor().getId())) {
            notificationService.createNotification(post.getAuthor(), NotificationType.COMMENT_REPLY, post, comment, null);
        }
        for (User u : subscriptionService.getPostSubscribers(postId)) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.POST_UPDATED, post, comment, null);
            }
        }
        for (User u : subscriptionService.getSubscribers(author.getUsername())) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.USER_ACTIVITY, post, comment, null);
            }
        }
        return comment;
    }

    public Comment addReply(String username, Long parentId, String content) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(parent.getPost());
        comment.setParent(parent);
        comment.setContent(content);
        comment = commentRepository.save(comment);
        if (!author.getId().equals(parent.getAuthor().getId())) {
            notificationService.createNotification(parent.getAuthor(), NotificationType.COMMENT_REPLY, parent.getPost(), comment, null);
        }
        for (User u : subscriptionService.getCommentSubscribers(parentId)) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.COMMENT_REPLY, parent.getPost(), comment, null);
            }
        }
        for (User u : subscriptionService.getPostSubscribers(parent.getPost().getId())) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.POST_UPDATED, parent.getPost(), comment, null);
            }
        }
        for (User u : subscriptionService.getSubscribers(author.getUsername())) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.USER_ACTIVITY, parent.getPost(), comment, null);
            }
        }
        return comment;
    }

    public List<Comment> getCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return commentRepository.findByPostAndParentIsNullOrderByCreatedAtAsc(post);
    }

    public List<Comment> getReplies(Long parentId) {
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return commentRepository.findByParentOrderByCreatedAtAsc(parent);
    }

    public List<Comment> getRecentCommentsByUser(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Pageable pageable = PageRequest.of(0, limit);
        return commentRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
    }

    public java.util.List<Comment> getCommentsByIds(java.util.List<Long> ids) {
        return commentRepository.findAllById(ids);
    }
}
