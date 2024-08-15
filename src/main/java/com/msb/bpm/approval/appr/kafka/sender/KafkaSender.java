package com.msb.bpm.approval.appr.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public abstract class KafkaSender implements MessageSender{

  private final KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper;

  protected KafkaSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> void sendMessage(T message, String topic) {
    String jsonStrData = JsonUtil.convertObject2String(message, objectMapper);
    if (StringUtils.isNotBlank(jsonStrData)) {
      kafkaTemplate.send(topic, jsonStrData);
      log.info("Sent message to topic {} successful", topic);
      log.info("Message data : {}", jsonStrData);
    } else {
      log.warn("Json can't parse object {}, kafka can't send message...", message);
    }
  }

  @Override
  public <T> void sendMessage(T message, String key, String topic) {
    String jsonStrData = JsonUtil.convertObject2String(message, objectMapper);
    if (StringUtils.isNotBlank(jsonStrData)) {
      kafkaTemplate.send(topic, key, jsonStrData);
      log.info("Sent message to topic {} successful", topic);
      log.info("Message data : {}", jsonStrData);
    } else {
      log.warn("Json can't parse object {}, kafka can't send message...", message);
    }
  }
}
