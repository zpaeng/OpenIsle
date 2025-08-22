package com.openisle.controller;

import com.openisle.dto.ChannelDto;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import com.openisle.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }

    @GetMapping
    public List<ChannelDto> listChannels(Authentication auth) {
        return channelService.listChannels(getCurrentUserId(auth));
    }

    @PostMapping("/{channelId}/join")
    public ChannelDto joinChannel(@PathVariable Long channelId, Authentication auth) {
        return channelService.joinChannel(channelId, getCurrentUserId(auth));
    }
}
