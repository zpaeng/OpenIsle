package com.openisle.repository;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(PostStatus status);
    List<Post> findByAuthorAndStatusOrderByCreatedAtDesc(User author, PostStatus status, Pageable pageable);
}
