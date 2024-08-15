package com.msb.bpm.approval.appr.config.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Getter
@EnableKafka
@Configuration
@RequiredArgsConstructor
@RefreshScope
public class CicDsKafkaConsumerConfig {

  @Value(value = "${cic.kafka-update-cic.consumer.topic}")
  private String topic;

  @Value(value = "${cic.kafka-update-cic.consumer.group-id}")
  private String groupId;

  private final CicDsKafkaProperties cicDsKafkaProperties;

  @Bean
  @Qualifier("cicDSConsumerFactory")
  public ConsumerFactory<String, String> cicDSConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(BOOTSTRAP_SERVERS_CONFIG, cicDsKafkaProperties.getBootstrapServers());

    props.put(SECURITY_PROTOCOL_CONFIG, cicDsKafkaProperties.getSecurityProtocol());
    props.put(SaslConfigs.SASL_MECHANISM, cicDsKafkaProperties.getSaslMechanism());
    props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
            "%s required username=\"%s\" " + "password=\"%s\";", ScramLoginModule.class.getName(),
            cicDsKafkaProperties.getUser(), cicDsKafkaProperties.getPassword()
    ));

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  @Qualifier("cicDsListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> cicDsListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cicDSConsumerFactory());
    return factory;
  }
}
