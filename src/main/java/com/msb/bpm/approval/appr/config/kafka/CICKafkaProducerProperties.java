package com.msb.bpm.approval.appr.config.kafka;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cic.kafka.producer")
public class CICKafkaProducerProperties {

  @JsonProperty("topics")
  private Map<String, String> topics;

}
