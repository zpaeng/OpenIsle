package com.openisle.controller;

import com.openisle.dto.PostSummaryDto;
import com.openisle.mapper.PostMapper;
import com.openisle.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints for administrators to manage posts.
 */
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping("/pending")
    public List<PostSummaryDto> pendingPosts() {
        return postService.listPendingPosts().stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/approve")
    public PostSummaryDto approve(@PathVariable Long id) {
        return postMapper.toSummaryDto(postService.approvePost(id));
    }

    @PostMapping("/{id}/reject")
    public PostSummaryDto reject(@PathVariable Long id) {
        return postMapper.toSummaryDto(postService.rejectPost(id));
    }

    @PostMapping("/{id}/pin")
    public PostSummaryDto pin(@PathVariable Long id) {
        return postMapper.toSummaryDto(postService.pinPost(id));
    }

    @PostMapping("/{id}/unpin")
    public PostSummaryDto unpin(@PathVariable Long id) {
        return postMapper.toSummaryDto(postService.unpinPost(id));
    }
}
