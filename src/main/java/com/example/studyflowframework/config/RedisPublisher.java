package com.example.studyflowframework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * Klasa do publikowania komunikatów w Redis (Pub).
 * Np. przy rejestracji usera – wywołujesz publishUserRegistration(email).
 */
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userRegistrationTopic;

    @Autowired
    public RedisPublisher(RedisTemplate<String, Object> redisTemplate,
                          ChannelTopic userRegistrationTopic) {
        this.redisTemplate = redisTemplate;
        this.userRegistrationTopic = userRegistrationTopic;
    }

    /**
     * Publikuj komunikat (e-mail usera) na kanał "user_registration".
     */
    public void publishUserRegistration(String userEmail) {
        redisTemplate.convertAndSend(userRegistrationTopic.getTopic(), userEmail);
    }
}
