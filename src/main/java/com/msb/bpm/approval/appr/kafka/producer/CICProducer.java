package com.msb.bpm.approval.appr.kafka.producer;

import com.msb.bpm.approval.appr.config.kafka.CICKafkaProducerProperties;
import com.msb.bpm.approval.appr.kafka.sender.CICKafkaSender;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.in.CicRequestMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CICProducer {

  public static final String CIC_IN_TOPIC = "topic-in";

  private final CICKafkaSender cicKafkaSender;
  private final CICKafkaProducerProperties cicKafkaProducerProperties;

  public void publish(CicRequestMessage requestMessageData) {
    cicKafkaSender.sendMessage(requestMessageData, getCicInTopic());
  }

  public String getCicInTopic() {
    return cicKafkaProducerProperties.getTopics().get(CIC_IN_TOPIC);
  }
}
