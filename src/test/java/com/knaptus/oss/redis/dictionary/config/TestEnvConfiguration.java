package com.knaptus.oss.redis.dictionary.config;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * Test env config
 *
 * @author Aditya Bhardwaj
 */
@Configuration
@Profile(value = "test")
@Ignore
public class TestEnvConfiguration {

    @Value("classpath:test-repository.properties")
    private Resource repoResource;

    @Bean
    public Properties repo() throws IOException {
        Properties properties = new Properties();
        properties.load(repoResource.getInputStream());
        return properties;
    }
}

