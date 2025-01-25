package com.example.studyflowframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Główna konfiguracja Redisa:
 *  - RedisTemplate (String -> Object),
 *  - Topic (kanał) do publikacji,
 *  - ListenerContainer + MessageListenerAdapter.
 */
@Configuration
public class RedisConfig {

    /**
     * Kanał, na którym publikujemy info o rejestracji użytkownika.
     * Nazwę "user_registration" możesz dowolnie zmienić.
     */
    @Bean
    public ChannelTopic userRegistrationTopic() {
        return new ChannelTopic("user_registration");
    }

    /**
     * RedisTemplate – używane do publish/subscribe.
     * Tutaj klucze i wartości są serializowane jako String,
     * ale generyki <String, Object>.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Serializatory tekstowe (unikamy krzaczków).
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);

        // Opcjonalnie także hashKey, hashValue:
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        // Inicjalizacja
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Kontener subskrybenta – który nasłuchuje kanału w Redis
     * i wywołuje listenerAdapter, gdy nadejdzie wiadomość.
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic userRegistrationTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // Subskrybujemy konkretny kanał "user_registration":
        container.addMessageListener(listenerAdapter, userRegistrationTopic);
        return container;
    }

    /**
     * Adapter, który mapuje przychodzące wiadomości z Redis
     * na wywołanie metody "onMessage" w klasie RedisSubscriber.
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}
