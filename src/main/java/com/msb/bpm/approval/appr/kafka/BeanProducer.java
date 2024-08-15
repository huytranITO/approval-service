package com.msb.bpm.approval.appr.kafka;

import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 6/12/2023, Wednesday
 **/
@RequiredArgsConstructor
public class BeanProducer {

  private final ConcurrentMap<String, Object> metadata;
  private final ProducerStrategy producerStrategy;

  public void execute() {
    producerStrategy.publishMessage(metadata);
  }
  public void executeEvent() {
    producerStrategy.publishMessageEvent(metadata);
  }
}
