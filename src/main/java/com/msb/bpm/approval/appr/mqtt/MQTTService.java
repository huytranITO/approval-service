package com.msb.bpm.approval.appr.mqtt;

import java.util.Set;

public interface MQTTService {

  void sendCICMessage(Set<String> users, String applicationId);

  void sendCICMessage(String processingUserName, String applicationId);

  void sendCICMessageUpdate(Set<String> users, String applicationId, String message);

  void sendGenerateFormTemplateMessage(String user, String applicationId);
}
