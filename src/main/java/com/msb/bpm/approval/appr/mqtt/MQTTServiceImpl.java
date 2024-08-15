package com.msb.bpm.approval.appr.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.mqtt.MqttClient;
import com.msb.bpm.approval.appr.client.mqtt.MqttClientProperties;
import com.msb.bpm.approval.appr.mqtt.data.CICRatioMessageData;
import com.msb.bpm.approval.appr.mqtt.data.GenerateFormTemplateContent;
import com.msb.bpm.approval.appr.mqtt.data.GenerateFormTemplateMessageData;
import com.msb.bpm.approval.appr.mqtt.data.MessageData;
import java.util.Set;

import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class MQTTServiceImpl implements MQTTService {

  private final MqttClientProperties properties;
  private final MqttClient mqttClient;
  private final ObjectMapper objectMapper;


  @Override
  public void sendCICMessage(Set<String> users, String applicationId) {
    if (CollectionUtils.isEmpty(users)
        || !StringUtils.hasText(applicationId)) {
      return;
    }
    for (String user : users) {
      sendCICMessage(user, applicationId);
    }
  }

  @Override
  public void sendCICMessage(String processingUserName, String applicationId) {
    CICRatioMessageData cicRatioMessageData = new CICRatioMessageData(applicationId,
        processingUserName);
    sendMessage(processingUserName, cicRatioMessageData);
  }

  @Override
  public void sendCICMessageUpdate(Set<String> users, String applicationId, String message) {
    try {
      CICRatioMessageData cicRatioMessageData = new CICRatioMessageData(applicationId);
      cicRatioMessageData.setContentMessage(message);
      for (String user : users) {
        cicRatioMessageData.setProcessingUserName(user);
        sendMessage(user, cicRatioMessageData);
      }
    } catch (Exception e) {
      log.error("sendCICMessageUpdate with cause {}", e);
    }
  }

  @Override
  public void sendGenerateFormTemplateMessage(String user, String applicationId) {
    if (ObjectUtils.isEmpty(user)
            || !StringUtils.hasText(applicationId)) {
      return;
    }
    GenerateFormTemplateContent messageContent = new GenerateFormTemplateContent(applicationId, user);

    GenerateFormTemplateMessageData messageData = new GenerateFormTemplateMessageData(applicationId, user);
    messageData.setContentMessage(JsonUtil.convertObject2String(messageContent, objectMapper));

    sendMessage(user, messageData);
  }

  private void sendMessage(String userName, MessageData messageData) {
    try {
      String topic = buildTopic(userName);
      String message = messageData.buildMessage();
      log.info("[MQTT] send message MQTT: topic {}, message {}", topic, message);
      mqttClient.publish(topic, message);
    } catch (Exception ex) {
      log.error("[MQTT] public message fail! " + ex.getMessage());
    }
  }

  private String buildTopic(String userName) {
    return String.format(properties.getTopicPattern(), userName);
  }
}
