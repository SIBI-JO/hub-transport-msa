package com.sibijo.ai.application.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "slack.api")
@Data
public class SlackApiProperties {
    private String url;
    private String token;
}
