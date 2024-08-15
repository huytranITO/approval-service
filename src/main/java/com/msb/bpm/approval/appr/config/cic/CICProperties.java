package com.msb.bpm.approval.appr.config.cic;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cic")
public class CICProperties {

  @JsonProperty("enabled")
  private boolean enabled;

  @JsonProperty("validity_period")
  private Integer validityPeriod = 30;

  @JsonProperty("username")
  private String username;

  @JsonProperty("criteria_code_mapping")
  private Map<String, String> criteriaCodeMapping;

  private List<String> acceptedSubmitStatus;

}
