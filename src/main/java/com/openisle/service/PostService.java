package com.openisle.service;

import com.openisle.model.Post;
import com.openisle.model.User;
import com.openisle.model.Category;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import com.openisle.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Post createPost(String username, Long categoryId, String title, String content) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setCategory(category);
        return postRepository.save(post);
    }

    public Post getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setViews(post.getViews() + 1);
        return postRepository.save(post);
    }

    public List<Post> listPosts() {
        return postRepository.findAll();
    }
}
