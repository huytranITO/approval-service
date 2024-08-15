package com.msb.bpm.approval.appr.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.cifi")
public class CIFIConfigProperties {
    private String baseUrl;
    private String completeStatus;
    private String searchUrl;
}
