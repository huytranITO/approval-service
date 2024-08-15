package com.msb.bpm.approval.appr.config.kafka;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/9/2023, Sunday
 **/

@Configuration
@RequiredArgsConstructor
public class ExternalKafkaProducerConfig {

  private final ApplicationConfig applicationConfig;

  @Value(value = "${spring.kafka.properties.security.protocol}")
  private String protocol;

  @Value(value = "${spring.kafka.properties.sasl.mechanism}")
  private String mechanism;

  @Value(value = "${spring.kafka.properties.sasl.jaas.config}")
  private String mechanismConfig;

  @Bean("externalKafkaProducerConfigs")
  public Map<String, Object> externalKafkaProducerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        applicationConfig.getKafka().getExternal().getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
    props.put(SaslConfigs.SASL_MECHANISM, mechanism);
    props.put(SaslConfigs.SASL_JAAS_CONFIG, mechanismConfig);
    return props;
  }

  @Bean("externalKafkaProducerFactory")
  public ProducerFactory<String, String> externalKafkaProducerFactory() {
    return new DefaultKafkaProducerFactory<>(externalKafkaProducerConfigs());
  }

  @Bean("externalKafkaTemplate")
  public KafkaTemplate<String, String> externalKafkaTemplate() {
    return new KafkaTemplate<>(externalKafkaProducerFactory());
  }
}
