package com.msb.bpm.approval.appr.config.resttemplate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.rest-template")
public class RestTemplateProperties {

  private int connectionTimeout = 10;

  private int readTimeout = 60;
}
