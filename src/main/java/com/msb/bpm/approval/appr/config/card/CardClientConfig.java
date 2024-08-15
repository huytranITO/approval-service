package com.msb.bpm.approval.appr.config.card;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CardClientConfig {
  @Bean
  public RestTemplate cardRestTemplate() {
    return new RestTemplate();
  }

}
