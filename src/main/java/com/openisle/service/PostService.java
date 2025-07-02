package com.openisle.service;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.PublishMode;
import com.openisle.model.User;
import com.openisle.model.Category;
import com.openisle.model.NotificationType;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import com.openisle.repository.CategoryRepository;
import com.openisle.repository.TagRepository;
import com.openisle.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PublishMode publishMode;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;

    @org.springframework.beans.factory.annotation.Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       NotificationService notificationService,
                       SubscriptionService subscriptionService,
                       @Value("${app.post.publish-mode:DIRECT}") PublishMode publishMode) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.publishMode = publishMode;
    }

    public Post createPost(String username,
                           Long categoryId,
                           String title,
                           String content,
                           java.util.List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new IllegalArgumentException("At least one tag required");
        }
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        java.util.List<com.openisle.model.Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.isEmpty()) {
            throw new IllegalArgumentException("Tag not found");
        }
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setCategory(category);
        post.setTags(new java.util.HashSet<>(tags));
        post.setStatus(publishMode == PublishMode.REVIEW ? PostStatus.PENDING : PostStatus.PUBLISHED);
        post = postRepository.save(post);
        // notify followers of author
        for (User u : subscriptionService.getSubscribers(author.getUsername())) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(u, NotificationType.USER_ACTIVITY, post, null, null);
            }
        }
        return post;
    }

    public Post viewPost(Long id, String viewer) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new IllegalArgumentException("Post not found");
        }
        post.setViews(post.getViews() + 1);
        post = postRepository.save(post);
        if (viewer != null && !viewer.equals(post.getAuthor().getUsername())) {
            notificationService.createNotification(post.getAuthor(), NotificationType.POST_VIEWED, post, null, null);
        }
        return post;
    }

    public List<Post> listPosts() {
        return listPostsByCategories(null, null, null);
    }

    public List<Post> listPostsByCategories(java.util.List<Long> categoryIds,
                                            Integer page,
                                            Integer pageSize) {
        Pageable pageable = null;
        if (page != null && pageSize != null) {
            pageable = PageRequest.of(page, pageSize);
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            if (pageable != null) {
                return postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
            }
            return postRepository.findByStatus(PostStatus.PUBLISHED);
        }

        java.util.List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (pageable != null) {
            return postRepository.findByCategoryInAndStatus(categories, PostStatus.PUBLISHED, pageable);
        }
        return postRepository.findByCategoryInAndStatus(categories, PostStatus.PUBLISHED);
    }

    public List<Post> getRecentPostsByUser(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findByAuthorAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED, pageable);
    }

    public List<Post> listPostsByTags(java.util.List<Long> tagIds,
                                      Integer page,
                                      Integer pageSize) {
        if (tagIds == null || tagIds.isEmpty()) {
            return java.util.List.of();
        }

        Pageable pageable = null;
        if (page != null && pageSize != null) {
            pageable = PageRequest.of(page, pageSize);
        }

        java.util.List<com.openisle.model.Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.isEmpty()) {
            return java.util.List.of();
        }

        if (pageable != null) {
            return postRepository.findDistinctByTagsInAndStatus(tags, PostStatus.PUBLISHED, pageable);
        }
        return postRepository.findDistinctByTagsInAndStatus(tags, PostStatus.PUBLISHED);
    }

    public List<Post> listPendingPosts() {
        return postRepository.findByStatus(PostStatus.PENDING);
    }

    public Post approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setStatus(PostStatus.PUBLISHED);
        post = postRepository.save(post);
        notificationService.createNotification(post.getAuthor(), NotificationType.POST_REVIEWED, post, null, true);
        return post;
    }

    public Post rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setStatus(PostStatus.REJECTED);
        post = postRepository.save(post);
        notificationService.createNotification(post.getAuthor(), NotificationType.POST_REVIEWED, post, null, false);
        return post;
    }
}
