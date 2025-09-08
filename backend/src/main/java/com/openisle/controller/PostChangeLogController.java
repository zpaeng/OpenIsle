package com.openisle.controller;

import com.openisle.dto.PostChangeLogDto;
import com.openisle.mapper.PostChangeLogMapper;
import com.openisle.service.PostChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostChangeLogController {
    private final PostChangeLogService changeLogService;
    private final PostChangeLogMapper mapper;

    @GetMapping("/{id}/change-logs")
    public List<PostChangeLogDto> listLogs(@PathVariable Long id) {
        return changeLogService.listLogs(id).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
