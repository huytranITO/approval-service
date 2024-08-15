package com.msb.bpm.approval.appr.client.camunda;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 14/7/2023, Friday
 **/
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "camunda.bpm.client")
@Getter
@Setter
public class CamundaProperties {

  private Map<String, ExternalTaskSubscription> subscriptions;
}
