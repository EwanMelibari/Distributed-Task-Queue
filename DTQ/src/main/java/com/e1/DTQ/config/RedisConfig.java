package com.e1.DTQ.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

// Configuration class for setting up RedisTemplate to interact with Redis data store
@Configuration
public class RedisConfig {
    // Method to create and configure a RedisTemplate bean for interacting with Redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. Create a Jackson ObjectMapper for serializing and deserializing Java objects to JSON
        ObjectMapper mapper = JsonMapper.builder().build();

        // 2. Create a GenericJacksonJsonRedisSerializer using the ObjectMapper to handle JSON serialization for Redis values
        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(mapper); // most error-prone part, ensure correct configuration of ObjectMapper for complex types and date handling if needed

        // 3. Configure the RedisTemplate to use StringRedisSerializer for keys and the GenericJacksonJsonRedisSerializer for values and hash values
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template; // sounds good, but be cautious of potential serialization issues with complex objects or date/time types, ensure ObjectMapper is configured correctly for those cases
    }
    
}
