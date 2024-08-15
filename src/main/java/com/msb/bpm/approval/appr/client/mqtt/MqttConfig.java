package com.msb.bpm.approval.appr.client.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {
  private final MqttClientProperties mqttProperties;
}
