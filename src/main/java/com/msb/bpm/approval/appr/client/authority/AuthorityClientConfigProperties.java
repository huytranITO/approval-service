package com.msb.bpm.approval.appr.client.authority;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.authority")
public class AuthorityClientConfigProperties {

  private String baseUrl;

  private Map<String, AuthorityEndpointProperties> endpoint;

}