package com.openisle.controller;

import com.openisle.dto.PostSummaryDto;
import com.openisle.dto.SearchResultDto;
import com.openisle.dto.UserDto;
import com.openisle.mapper.PostMapper;
import com.openisle.mapper.UserMapper;
import com.openisle.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping("/users")
    public List<UserDto> searchUsers(@RequestParam String keyword) {
        return searchService.searchUsers(keyword).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts")
    public List<PostSummaryDto> searchPosts(@RequestParam String keyword) {
        return searchService.searchPosts(keyword).stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/content")
    public List<PostSummaryDto> searchPostsByContent(@RequestParam String keyword) {
        return searchService.searchPostsByContent(keyword).stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/title")
    public List<PostSummaryDto> searchPostsByTitle(@RequestParam String keyword) {
        return searchService.searchPostsByTitle(keyword).stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/global")
    public List<SearchResultDto> global(@RequestParam String keyword) {
        return searchService.globalSearch(keyword).stream()
                .map(r -> {
                    SearchResultDto dto = new SearchResultDto();
                    dto.setType(r.type());
                    dto.setId(r.id());
                    dto.setText(r.text());
                    dto.setSubText(r.subText());
                    dto.setExtra(r.extra());
                    dto.setPostId(r.postId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
