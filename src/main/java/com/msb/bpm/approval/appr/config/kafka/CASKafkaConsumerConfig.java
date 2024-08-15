package com.msb.bpm.approval.appr.config.kafka;

import lombok.Getter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Getter
@EnableKafka
@Configuration
public class CASKafkaConsumerConfig {

  @Autowired
  private CASKafkaProperties casKafkaProperties;

  @Value(value = "${cas.kafka.consumer.group-id}")
  private String groupId;

  @Value(value = "${cas.kafka.consumer.topic}")
  private String topic;

  @Bean
  @Qualifier("casConsumerFactory")
  public ConsumerFactory<String, String> casConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, casKafkaProperties.getBootstrapServers());
    props.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            LongDeserializer.class);
    props.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ByteArrayDeserializer.class);

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, casKafkaProperties.getSecurityProtocol());
    props.put(SaslConfigs.SASL_MECHANISM,  casKafkaProperties.getSaslMechanism());
    props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
            "%s required username=\"%s\" " + "password=\"%s\";",
            ScramLoginModule.class.getName(),
            casKafkaProperties.getUser(),
            casKafkaProperties.getPassword()
    ));

    return new DefaultKafkaConsumerFactory<>(props);
  }
  @Bean
  @Qualifier("casListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> casListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(casConsumerFactory());
    return factory;
  }

}
