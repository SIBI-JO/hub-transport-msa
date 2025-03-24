package com.sibijo.ai.application.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "slack")
@Data
public class SlackApiProperties {
    // properties 파일의 slack_api_url -> slack.apiUrl, slack_api_token -> slack.apiToken,
    // slack_channel -> slack.channel
    private String apiUrl;
    private String apiToken;
    private String channel;
}

