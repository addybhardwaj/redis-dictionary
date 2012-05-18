package com.intelladept.oss.redis.dictionary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis based repository configuration
 *
 * @author Aditya Bhardwaj
 */
@Configuration
@ComponentScan(basePackages = {
        "com.intelladept.oss.redis.dictionary"
})
public class RepositoryConfiguration {

    @Value("#{repo.redishost}")
    private String hostname;

    @Value("#{repo.redisport}")
    private int port;

    @Value("#{repo.redispass}")
    private String password;


    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate stringRedisTemplate() {
        RedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(hostname);
        redisConnectionFactory.setPort(port);
        redisConnectionFactory.setPassword(password);
        redisConnectionFactory.setPoolConfig(poolConfig());
        return redisConnectionFactory;
    }
    
    @Bean
    public JedisPoolConfig poolConfig() {
    	JedisPoolConfig poolConfig = new JedisPoolConfig();
    	poolConfig.setMaxActive(4);
    	poolConfig.setMaxWait(30000); //30sec
    	return poolConfig;
    }


}
