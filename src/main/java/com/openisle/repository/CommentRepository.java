package com.openisle.repository;

import com.openisle.model.Comment;
import com.openisle.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.openisle.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentIsNullOrderByCreatedAtAsc(Post post);
    List<Comment> findByParentOrderByCreatedAtAsc(Comment parent);
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    List<Comment> findByContentContainingIgnoreCase(String keyword);
}
