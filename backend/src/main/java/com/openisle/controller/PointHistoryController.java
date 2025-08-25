package com.openisle.controller;

import com.openisle.dto.PointHistoryDto;
import com.openisle.mapper.PointHistoryMapper;
import com.openisle.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point-histories")
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointService pointService;
    private final PointHistoryMapper pointHistoryMapper;

    @GetMapping
    public List<PointHistoryDto> list(Authentication auth) {
        return pointService.listHistory(auth.getName()).stream()
                .map(pointHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
