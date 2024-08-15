package com.msb.bpm.approval.appr.client.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.smart-chat")
public class SmartChatConfigProperties {

    private String baseUrl;

    private Map<String, SmartChatEndpointProperties> endpoint;

}
