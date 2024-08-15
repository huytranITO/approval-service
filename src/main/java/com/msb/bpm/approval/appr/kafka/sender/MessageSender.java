package com.msb.bpm.approval.appr.kafka.sender;

public interface MessageSender {

  <T> void sendMessage(T message, String topic);

  <T> void sendMessage(T message, String key, String topic);
}
