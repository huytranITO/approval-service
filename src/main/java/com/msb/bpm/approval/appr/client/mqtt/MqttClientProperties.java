package com.msb.bpm.approval.appr.client.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttClientProperties {
  private String serverUrl;
  private String username;
  private String password;
  private int connectionTimeout = 10;
  private int version = 5;
  private boolean autoReconnect = true;
  private String clientId = "notification-management";
  private int qos;
  private String topicPattern;
  private Integer maxNumberOfReconnect = 10;
}