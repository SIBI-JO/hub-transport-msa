package com.sibijo.ai.application.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini.api")
@Data
public class GeminiApiProperties {
    private String url;
    private String key;
}

