package com.msb.bpm.approval.appr.client.rule;


import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.rule")
public class RuleClientConfigProperties {

  private String baseUrl;

  private Map<String, RuleEndpointProperties> endpoint;
}
