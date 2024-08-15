package com.msb.bpm.approval.appr.client.usermanager.v2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UserManagerConfig {
  @Bean
  public RestTemplate userManagerRestTemplate() {
    return new RestTemplate();
  }
}
