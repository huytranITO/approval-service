package com.msb.bpm.approval.appr.kafka.producer;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_FEEDBACK_APPLICATION_CMS;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.kafka.sender.DataKafkaSender;
import com.msb.bpm.approval.appr.kafka.sender.ExternalKafkaSender;
import com.msb.bpm.approval.appr.model.dto.feedback.FeedbackDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataKafkaProducer {

  private final DataKafkaSender dataKafkaSender;
  private final ApplicationConfig config;

  public void publishMessage(FeedbackDTO feedbackDTO) {
    dataKafkaSender.sendMessage(feedbackDTO, getBpmWaitCustomerEditingTopic());
  }

  public String getBpmWaitCustomerEditingTopic() {
    return config.getKafka().getTopic().get(BPM_FEEDBACK_APPLICATION_CMS).getTopicName();
  }
}
