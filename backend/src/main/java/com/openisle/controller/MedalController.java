package com.openisle.controller;

import com.openisle.dto.MedalDto;
import com.openisle.service.MedalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medals")
@RequiredArgsConstructor
public class MedalController {
    private final MedalService medalService;

    @GetMapping
    public List<MedalDto> getMedals(@RequestParam(value = "userId", required = false) Long userId) {
        return medalService.getMedals(userId);
    }
}
