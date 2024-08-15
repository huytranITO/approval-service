package com.msb.bpm.approval.appr.config.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cas.kafka")
public class CASKafkaProperties {

  @JsonProperty("bootstrap-servers")
  private String bootstrapServers;

  @JsonProperty("security-protocol")
  private String securityProtocol;

  @JsonProperty("sasl-mechanism")
  private String saslMechanism;

  private String user;

  private String password;

  @JsonProperty("user-header")
  private UserHeader userHeader;

}
