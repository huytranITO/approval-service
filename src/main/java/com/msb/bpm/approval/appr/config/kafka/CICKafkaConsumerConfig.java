package com.msb.bpm.approval.appr.config.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Getter
@EnableKafka
@Configuration
public class CICKafkaConsumerConfig {

  @Autowired
  private CICKafkaProperties cicKafkaProperties;

  @Value(value = "${cic.kafka.consumer.group-id}")
  private String groupId;

  @Value(value = "${cic.kafka.consumer.topic}")
  private String topic;

  @Bean
  public ConsumerFactory<String, String> cicConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        cicKafkaProperties.getBootstrapServers());
    props.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        LongDeserializer.class);
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        ByteArrayDeserializer.class);

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
        cicKafkaProperties.getSecurityProtocol());
    props.put(SaslConfigs.SASL_MECHANISM, cicKafkaProperties.getSaslMechanism());
    props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
        "%s required username=\"%s\" " + "password=\"%s\";",
        ScramLoginModule.class.getName(),
        cicKafkaProperties.getUser(),
        cicKafkaProperties.getPassword()
    ));

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> cicKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cicConsumerFactory());
    return factory;
  }

}
