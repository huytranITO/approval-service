package com.msb.bpm.approval.appr.config;

import java.util.Map;
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
@ConfigurationProperties(value = "pilot")
public class LandingPageConfig {
  public static final String CJBO_SOURCE = "CJBO";
  public static final String CJMHOME_SOURCE = "CJMHome";
  public static final String BPM_SOURCE = "BPM";
  private Ldp ldp;
  private boolean enable;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Ldp {
    public Map<String, DataList> source;

    @Data
    public static class DataList {
      private String[] list;
    }
  }


}
