package com.msb.bpm.approval.appr.client.mqtt;

public interface PublishMessage {

  void publish(String topic, String message);

}
