package com.msb.bpm.approval.appr.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@AllArgsConstructor
@RefreshScope
@NoArgsConstructor
@ConfigurationProperties(value = "approval-service.cache")
public class ApplicationDurationConfig {

  private Long categoryDataDuration;

  private Long mercuryDataDuration;

  private Long esbAccountDuration;
}
