package com.msb.bpm.approval.appr.kafka;

import java.util.concurrent.ConcurrentMap;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 5/5/2023, Friday
 **/
public interface ProducerStrategy {
  void publishMessage(String topicName);

  void publishMessage(ConcurrentMap<String, Object> metadata);

  void publishMessageEvent(ConcurrentMap<String, Object> metadata);
}
