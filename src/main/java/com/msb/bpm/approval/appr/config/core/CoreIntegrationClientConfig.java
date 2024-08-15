package com.msb.bpm.approval.appr.config.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CoreIntegrationClientConfig {

  @Bean
  public RestTemplate coreIntegrationRestTemplate() {
    return new RestTemplate();
  }

}
