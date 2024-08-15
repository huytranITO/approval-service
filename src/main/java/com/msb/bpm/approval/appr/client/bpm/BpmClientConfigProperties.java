package com.msb.bpm.approval.appr.client.bpm;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.bpm-operation")
public class BpmClientConfigProperties {

  private String baseUrl;

  private Map<String, BpmEndpointProperties> endpoint;
}
