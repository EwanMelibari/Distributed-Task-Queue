package com.e1.DTQ.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    ObjectMapper mapper = new ObjectMapper();
    // دعم الوقت للـ Records
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    
    // تفعيل معلومات النوع لمنع الـ ClassCastException الذي رأيناه سابقاً
    // في ملف RedisConfig.java
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance, 
        ObjectMapper.DefaultTyping.EVERYTHING, 
        JsonTypeInfo.As.PROPERTY
    );

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    
    return template;
}
}