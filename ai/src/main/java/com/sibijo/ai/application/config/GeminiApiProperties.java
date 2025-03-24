package com.sibijo.ai.application.config;




import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini")
@Data
public class GeminiApiProperties {
    // properties 파일의 gemini_api_url -> apiUrl, gemini_api_key -> apiKey
    private String apiUrl;
    private String apiKey;
}

