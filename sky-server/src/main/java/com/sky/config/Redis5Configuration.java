package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class Redis5Configuration {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("创建redis对象");
        //设置redis连接工厂对象
        RedisTemplate redisContect = new RedisTemplate();
        redisContect.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器
        redisContect.setKeySerializer(new StringRedisSerializer());
        return redisContect;
    }
}
