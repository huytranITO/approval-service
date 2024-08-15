package com.msb.bpm.approval.appr.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 7/5/2023, Sunday
 **/
@Component
@Slf4j
public class GeneralKafkaSender extends KafkaSender {
  public GeneralKafkaSender(
      @Qualifier(value = "generalKafkaTemplate") KafkaTemplate<String, String> generalKafkaTemplate,
      @Qualifier(value = "objectMapperWithNull") ObjectMapper objectMapper) {
    super(generalKafkaTemplate, objectMapper);
  }

}
