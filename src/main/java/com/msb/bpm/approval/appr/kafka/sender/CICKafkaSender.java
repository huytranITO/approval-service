package com.msb.bpm.approval.appr.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CICKafkaSender extends KafkaSender {
  public CICKafkaSender(
      @Qualifier(value = "cicKafkaTemplate") KafkaTemplate<String, String> cicKafkaTemplate,
      @Qualifier(value = "objectMapperWithNull") ObjectMapper objectMapper) {
    super(cicKafkaTemplate, objectMapper);
  }

}
