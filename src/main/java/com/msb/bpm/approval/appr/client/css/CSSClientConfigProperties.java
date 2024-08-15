package com.msb.bpm.approval.appr.client.css;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.css")
public class CSSClientConfigProperties {

  private String baseUrl;

  private String userAuthen;

  private String password;

  private Map<String, CSSEndpointProperties> endpoint;

}
