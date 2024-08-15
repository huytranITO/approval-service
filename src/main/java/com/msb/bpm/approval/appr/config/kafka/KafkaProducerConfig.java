package com.msb.bpm.approval.appr.config.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value(value = "${spring.kafka.properties.security.protocol}")
  private String protocol;

  @Value(value = "${spring.kafka.properties.sasl.mechanism}")
  private String mechanism;

  @Value(value = "${spring.kafka.properties.sasl.jaas.config}")
  private String mechanismConfig;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
    configProps.put(SaslConfigs.SASL_MECHANISM, mechanism);
    configProps.put(SaslConfigs.SASL_JAAS_CONFIG, mechanismConfig);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  @Qualifier(value = "generalKafkaTemplate")
  public KafkaTemplate<String, String> generalKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}