package com.jhome.user.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String username, String token) {
        String key = "username:" + username;
        redisTemplate.opsForHash().put(key, "token", token);
        redisTemplate.expire(key, Duration.ofHours(1));
    }
}
