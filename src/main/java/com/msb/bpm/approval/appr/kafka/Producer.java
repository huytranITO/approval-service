package com.msb.bpm.approval.appr.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 6/5/2023, Saturday
 * @deprecated since 06/12/2023
 **/
@Deprecated
@RequiredArgsConstructor
@Slf4j
public class Producer {
  private final String topicName;

  public void produce(ProducerStrategy strategy) {
    log.info("Send message to topic {}.....", topicName);
    strategy.publishMessage(topicName);
  }
}
