package com.msb.bpm.approval.appr.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataKafkaSender extends KafkaSender {


  public DataKafkaSender(
      @Qualifier("dataKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper) {
    super(kafkaTemplate, objectMapper);
  }
}
