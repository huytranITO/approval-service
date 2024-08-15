package com.msb.bpm.approval.appr.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.configuration")
public class ConfigurationClientProperties {

  @JsonProperty("base_url")
  private String baseUrl;

  @JsonProperty("endpoint")
  private Map<String, String> endpoint;
}
