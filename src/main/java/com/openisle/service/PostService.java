package com.openisle.service;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.PublishMode;
import com.openisle.model.User;
import com.openisle.model.Category;
import com.openisle.model.Comment;
import com.openisle.model.NotificationType;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import com.openisle.repository.CategoryRepository;
import com.openisle.repository.TagRepository;
import com.openisle.service.SubscriptionService;
import com.openisle.service.CommentService;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.ReactionRepository;
import com.openisle.repository.PostSubscriptionRepository;
import com.openisle.repository.NotificationRepository;
import com.openisle.model.Role;
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
    private PublishMode publishMode;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final PostSubscriptionRepository postSubscriptionRepository;
    private final NotificationRepository notificationRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       NotificationService notificationService,
                       SubscriptionService subscriptionService,
                       CommentService commentService,
                       CommentRepository commentRepository,
                       ReactionRepository reactionRepository,
                       PostSubscriptionRepository postSubscriptionRepository,
                       NotificationRepository notificationRepository,
                       @Value("${app.post.publish-mode:DIRECT}") PublishMode publishMode) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
        this.postSubscriptionRepository = postSubscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.publishMode = publishMode;
    }

    public PublishMode getPublishMode() {
        return publishMode;
    }

    public void setPublishMode(PublishMode publishMode) {
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
        if (tagIds.size() > 2) {
            throw new IllegalArgumentException("At most two tags allowed");
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
        if (post.getStatus() == PostStatus.PENDING) {
            java.util.List<User> admins = userRepository.findByRole(com.openisle.model.Role.ADMIN);
            for (User admin : admins) {
                notificationService.createNotification(admin,
                        NotificationType.POST_REVIEW_REQUEST, post, null, null, author, null);
            }
            notificationService.createNotification(author,
                    NotificationType.POST_REVIEW_REQUEST, post, null, null, null, null);
        }
        // notify followers of author
        for (User u : subscriptionService.getSubscribers(author.getUsername())) {
            if (!u.getId().equals(author.getId())) {
                notificationService.createNotification(
                        u,
                        NotificationType.FOLLOWED_POST,
                        post,
                        null,
                        null,
                        author,
                        null);
            }
        }
        return post;
    }

    public Post viewPost(Long id, String viewer) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (post.getStatus() != PostStatus.PUBLISHED) {
            if (viewer == null) {
                throw new IllegalArgumentException("Post not found");
            }
            User viewerUser = userRepository.findByUsername(viewer)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (!viewerUser.getRole().equals(com.openisle.model.Role.ADMIN) && !viewerUser.getId().equals(post.getAuthor().getId())) {
                throw new IllegalArgumentException("Post not found");
            }
        }
        post.setViews(post.getViews() + 1);
        post = postRepository.save(post);
        if (viewer != null && !viewer.equals(post.getAuthor().getUsername())) {
            User viewerUser = userRepository.findByUsername(viewer).orElse(null);
            if (viewerUser != null) {
                notificationService.createNotification(post.getAuthor(), NotificationType.POST_VIEWED, post, null, null, viewerUser, null);
            } else {
                notificationService.createNotification(post.getAuthor(), NotificationType.POST_VIEWED, post, null, null);
            }
        }
        return post;
    }

    public List<Post> listPosts() {
        return listPostsByCategories(null, null, null);
    }

    public List<Post> listPostsByViews(Integer page, Integer pageSize) {
        return listPostsByViews(null, null, page, pageSize);
    }

    public List<Post> listPostsByViews(java.util.List<Long> categoryIds,
                                       java.util.List<Long> tagIds,
                                       Integer page,
                                       Integer pageSize) {
        Pageable pageable = null;
        if (page != null && pageSize != null) {
            pageable = PageRequest.of(page, pageSize);
        }

        boolean hasCategories = categoryIds != null && !categoryIds.isEmpty();
        boolean hasTags = tagIds != null && !tagIds.isEmpty();

        if (!hasCategories && !hasTags) {
            if (pageable != null) {
                return postRepository.findByStatusOrderByViewsDesc(PostStatus.PUBLISHED, pageable);
            }
            return postRepository.findByStatusOrderByViewsDesc(PostStatus.PUBLISHED);
        }

        if (hasCategories) {
            java.util.List<Category> categories = categoryRepository.findAllById(categoryIds);
            if (categories.isEmpty()) {
                return java.util.List.of();
            }
            if (hasTags) {
                java.util.List<com.openisle.model.Tag> tags = tagRepository.findAllById(tagIds);
                if (tags.isEmpty()) {
                    return java.util.List.of();
                }
                if (pageable != null) {
                    return postRepository.findByCategoriesAndAllTagsOrderByViewsDesc(
                            categories, tags, PostStatus.PUBLISHED, tags.size(), pageable);
                }
                return postRepository.findByCategoriesAndAllTagsOrderByViewsDesc(
                        categories, tags, PostStatus.PUBLISHED, tags.size());
            }
            if (pageable != null) {
                return postRepository.findByCategoryInAndStatusOrderByViewsDesc(categories, PostStatus.PUBLISHED, pageable);
            }
            return postRepository.findByCategoryInAndStatusOrderByViewsDesc(categories, PostStatus.PUBLISHED);
        }

        java.util.List<com.openisle.model.Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.isEmpty()) {
            return java.util.List.of();
        }
        if (pageable != null) {
            return postRepository.findByAllTagsOrderByViewsDesc(tags, PostStatus.PUBLISHED, tags.size(), pageable);
        }
        return postRepository.findByAllTagsOrderByViewsDesc(tags, PostStatus.PUBLISHED, tags.size());
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

    public java.time.LocalDateTime getLastPostTime(String username) {
        return postRepository.findLastPostTime(username);
    }

    public long getTotalViews(String username) {
        Long v = postRepository.sumViews(username);
        return v != null ? v : 0;
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
            return postRepository.findByAllTags(tags, PostStatus.PUBLISHED, tags.size(), pageable);
        }
        return postRepository.findByAllTags(tags, PostStatus.PUBLISHED, tags.size());
    }

    public List<Post> listPostsByCategoriesAndTags(java.util.List<Long> categoryIds,
                                                   java.util.List<Long> tagIds,
                                                   Integer page,
                                                   Integer pageSize) {
        if (categoryIds == null || categoryIds.isEmpty() || tagIds == null || tagIds.isEmpty()) {
            return java.util.List.of();
        }

        Pageable pageable = null;
        if (page != null && pageSize != null) {
            pageable = PageRequest.of(page, pageSize);
        }

        java.util.List<Category> categories = categoryRepository.findAllById(categoryIds);
        java.util.List<com.openisle.model.Tag> tags = tagRepository.findAllById(tagIds);
        if (categories.isEmpty() || tags.isEmpty()) {
            return java.util.List.of();
        }

        if (pageable != null) {
            return postRepository.findByCategoriesAndAllTags(categories, tags, PostStatus.PUBLISHED, tags.size(), pageable);
        }
        return postRepository.findByCategoriesAndAllTags(categories, tags, PostStatus.PUBLISHED, tags.size());
    }

    public List<Post> listPendingPosts() {
        return postRepository.findByStatus(PostStatus.PENDING);
    }

    public Post approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        // publish all pending tags along with the post
        for (com.openisle.model.Tag tag : post.getTags()) {
            if (!tag.isApproved()) {
                tag.setApproved(true);
                tagRepository.save(tag);
            }
        }
        post.setStatus(PostStatus.PUBLISHED);
        post = postRepository.save(post);
        notificationService.createNotification(post.getAuthor(), NotificationType.POST_REVIEWED, post, null, true);
        return post;
    }

    public Post rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        // remove user created tags that are only linked to this post
        java.util.Set<com.openisle.model.Tag> tags = new java.util.HashSet<>(post.getTags());
        for (com.openisle.model.Tag tag : tags) {
            if (!tag.isApproved()) {
                long count = postRepository.countDistinctByTags_Id(tag.getId());
                if (count <= 1) {
                    post.getTags().remove(tag);
                    tagRepository.delete(tag);
                }
            }
        }
        post.setStatus(PostStatus.REJECTED);
        post = postRepository.save(post);
        notificationService.createNotification(post.getAuthor(), NotificationType.POST_REVIEWED, post, null, false);
        return post;
    }

    @org.springframework.transaction.annotation.Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getId().equals(post.getAuthor().getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Unauthorized");
        }
        for (Comment c : commentRepository.findByPostAndParentIsNullOrderByCreatedAtAsc(post)) {
            commentService.deleteCommentCascade(c);
        }
        reactionRepository.findByPost(post).forEach(reactionRepository::delete);
        postSubscriptionRepository.findByPost(post).forEach(postSubscriptionRepository::delete);
        notificationRepository.findByPost(post).forEach(n -> { n.setPost(null); notificationRepository.save(n); });
        postRepository.delete(post);
    }

    public java.util.List<Post> getPostsByIds(java.util.List<Long> ids) {
        return postRepository.findAllById(ids);
    }

    public long countPostsByCategory(Long categoryId) {
        return postRepository.countByCategory_Id(categoryId);
    }

    public long countPostsByTag(Long tagId) {
        return postRepository.countDistinctByTags_Id(tagId);
    }
}
