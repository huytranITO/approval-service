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

@Configuration
@RequiredArgsConstructor
public class DataKafkaProducerConfig {

  private final ApplicationConfig applicationConfig;
  
  @Bean("dataKafkaProducerConfigs")
  public Map<String, Object> dataKafkaProducerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        applicationConfig.getKafka().getDataKafka().getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
        applicationConfig.getKafka().getDataKafka().getProtocol());
    props.put(SaslConfigs.SASL_MECHANISM,
        applicationConfig.getKafka().getDataKafka().getSaslMechanism());
    props.put(SaslConfigs.SASL_JAAS_CONFIG,
            "org.apache.kafka.common.security.scram.ScramLoginModule" +
                    " required serviceName=\"Kafka\" username=\"" + applicationConfig.getKafka()
                    .getDataKafka().getUser()
                    + "\" password=\"" + applicationConfig.getKafka().getDataKafka().getPassword() + "\";");
    return props;
  }

  @Bean("dataKafkaProducerFactory")
  public ProducerFactory<String, String> dataKafkaProducerFactory() {
    return new DefaultKafkaProducerFactory<>(dataKafkaProducerConfigs());
  }

  @Bean("dataKafkaTemplate")
  public KafkaTemplate<String, String> dataKafkaTemplate() {
    return new KafkaTemplate<>(dataKafkaProducerFactory());
  }
}
