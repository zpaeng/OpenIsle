package com.openisle.repository;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.User;
import com.openisle.model.Category;
import com.openisle.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(PostStatus status);
    List<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByAuthorAndStatusOrderByCreatedAtDesc(User author, PostStatus status, Pageable pageable);
    List<Post> findByCategoryInAndStatus(List<Category> categories, PostStatus status);
    List<Post> findByCategoryInAndStatus(List<Category> categories, PostStatus status, Pageable pageable);
    List<Post> findDistinctByTagsInAndStatus(List<Tag> tags, PostStatus status);
    List<Post> findDistinctByTagsInAndStatus(List<Tag> tags, PostStatus status, Pageable pageable);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(String titleKeyword, String contentKeyword, PostStatus status);
    List<Post> findByContentContainingIgnoreCaseAndStatus(String keyword, PostStatus status);
    List<Post> findByTitleContainingIgnoreCaseAndStatus(String keyword, PostStatus status);
}
