package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;

import com.msb.bpm.approval.appr.kafka.BeanProducer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushKafkaListener {

  private final ProducerStrategyFactory producerStrategyFactory;

  @Async
  @EventListener
  @TransactionalEventListener
  public void pushKafkaData(PushKafkaEvent pushKafkaEvent) {
    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    metadata.put(APPLICATION_BPM_IDS, pushKafkaEvent.getBpmIds());
    new BeanProducer(metadata, producerStrategyFactory.getProducerStrategy(
        pushKafkaEvent.getEventType())).executeEvent();
  }
}
