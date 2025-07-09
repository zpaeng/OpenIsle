package com.openisle.repository;

import com.openisle.model.Comment;
import com.openisle.model.Post;
import com.openisle.model.Reaction;
import com.openisle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPostAndType(User user, Post post, com.openisle.model.ReactionType type);
    Optional<Reaction> findByUserAndCommentAndType(User user, Comment comment, com.openisle.model.ReactionType type);
    List<Reaction> findByPost(Post post);
    List<Reaction> findByComment(Comment comment);

    @Query("SELECT r.post.id FROM Reaction r WHERE r.post IS NOT NULL AND r.post.author.username = :username AND r.type = com.openisle.model.ReactionType.LIKE GROUP BY r.post.id ORDER BY COUNT(r.id) DESC")
    List<Long> findTopPostIds(@Param("username") String username, Pageable pageable);

    @Query("SELECT r.comment.id FROM Reaction r WHERE r.comment IS NOT NULL AND r.comment.author.username = :username AND r.type = com.openisle.model.ReactionType.LIKE GROUP BY r.comment.id ORDER BY COUNT(r.id) DESC")
    List<Long> findTopCommentIds(@Param("username") String username, Pageable pageable);
}
