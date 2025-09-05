package com.openisle.controller;

import com.openisle.config.CachingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * @author smallclover
 * @since 2025-09-05
 * 统计在线人数
 */
@RestController
@RequestMapping("/api/online")
@RequiredArgsConstructor
public class OnlineController {

    private final RedisTemplate redisTemplate;
    private static final String ONLINE_KEY = CachingConfig.ONLINE_CACHE_NAME +":";

    @PostMapping("/heartbeat")
    public void ping(@RequestParam String userId){
        redisTemplate.opsForValue().set(ONLINE_KEY+userId,"1", Duration.ofSeconds(150));
    }

    @GetMapping("/count")
    public long count(){
        return redisTemplate.keys(ONLINE_KEY+"*").size();
    }
}
