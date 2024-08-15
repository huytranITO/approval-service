package com.msb.bpm.approval.appr.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/9/2023, Sunday
 **/
@Component
public class ExternalKafkaSender extends KafkaSender {


  public ExternalKafkaSender(
      @Qualifier("externalKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper) {
    super(kafkaTemplate, objectMapper);
  }
}
