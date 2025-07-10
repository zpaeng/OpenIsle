package com.openisle.repository;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.User;
import com.openisle.model.Category;
import com.openisle.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(PostStatus status);
    List<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByStatusOrderByViewsDesc(PostStatus status);
    List<Post> findByStatusOrderByViewsDesc(PostStatus status, Pageable pageable);
    List<Post> findByAuthorAndStatusOrderByCreatedAtDesc(User author, PostStatus status, Pageable pageable);
    List<Post> findByCategoryInAndStatus(List<Category> categories, PostStatus status);
    List<Post> findByCategoryInAndStatus(List<Category> categories, PostStatus status, Pageable pageable);
    List<Post> findDistinctByTagsInAndStatus(List<Tag> tags, PostStatus status);
    List<Post> findDistinctByTagsInAndStatus(List<Tag> tags, PostStatus status, Pageable pageable);
    List<Post> findDistinctByCategoryInAndTagsInAndStatus(List<Category> categories, List<Tag> tags, PostStatus status);
    List<Post> findDistinctByCategoryInAndTagsInAndStatus(List<Category> categories, List<Tag> tags, PostStatus status, Pageable pageable);

    List<Post> findByCategoryInAndStatusOrderByViewsDesc(List<Category> categories, PostStatus status);
    List<Post> findByCategoryInAndStatusOrderByViewsDesc(List<Category> categories, PostStatus status, Pageable pageable);
    List<Post> findDistinctByTagsInAndStatusOrderByViewsDesc(List<Tag> tags, PostStatus status);
    List<Post> findDistinctByTagsInAndStatusOrderByViewsDesc(List<Tag> tags, PostStatus status, Pageable pageable);
    List<Post> findDistinctByCategoryInAndTagsInAndStatusOrderByViewsDesc(List<Category> categories, List<Tag> tags, PostStatus status);
    List<Post> findDistinctByCategoryInAndTagsInAndStatusOrderByViewsDesc(List<Category> categories, List<Tag> tags, PostStatus status, Pageable pageable);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(String titleKeyword, String contentKeyword, PostStatus status);
    List<Post> findByContentContainingIgnoreCaseAndStatus(String keyword, PostStatus status);
    List<Post> findByTitleContainingIgnoreCaseAndStatus(String keyword, PostStatus status);

    @Query("SELECT MAX(p.createdAt) FROM Post p WHERE p.author.username = :username AND p.status = com.openisle.model.PostStatus.PUBLISHED")
    LocalDateTime findLastPostTime(@Param("username") String username);

    @Query("SELECT SUM(p.views) FROM Post p WHERE p.author.username = :username AND p.status = com.openisle.model.PostStatus.PUBLISHED")
    Long sumViews(@Param("username") String username);

    long countByCategory_Id(Long categoryId);

    long countDistinctByTags_Id(Long tagId);
}
