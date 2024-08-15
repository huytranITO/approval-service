package com.msb.bpm.approval.appr.config.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Getter
@Configuration
@AllArgsConstructor
public class CICKafkaProducerConfig {

  private final CICKafkaProperties cicKafkaProperties;

  @Bean
  @Qualifier(value = "cicProducerFactory")
  public ProducerFactory<String, String> cicProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cicKafkaProperties.getBootstrapServers());
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
        cicKafkaProperties.getSecurityProtocol());
    configProps.put(SaslConfigs.SASL_MECHANISM, cicKafkaProperties.getSaslMechanism());
    configProps.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
        "%s required username=\"%s\" " + "password=\"%s\";",
        ScramLoginModule.class.getName(),
        cicKafkaProperties.getUser(),
        cicKafkaProperties.getPassword()
    ));

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  @Qualifier(value = "cicKafkaTemplate")
  public KafkaTemplate<String, String> cicKafkaTemplate() {
    return new KafkaTemplate<>(cicProducerFactory());
  }
}
