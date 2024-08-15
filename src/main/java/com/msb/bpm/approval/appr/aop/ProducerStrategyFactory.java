package com.msb.bpm.approval.appr.aop;

import static com.msb.bpm.approval.appr.aop.EventType.CMS_INFO;
import static com.msb.bpm.approval.appr.aop.EventType.CMS_V2_INFO;
import static com.msb.bpm.approval.appr.aop.EventType.GENERAL_INFO;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.kafka.ProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsInfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsV2InfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProducerStrategyFactory {

  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;
  private final CmsInfoProducerStrategy cmsInfoProducerStrategy;
  private final CmsV2InfoProducerStrategy cmsV2InfoProducerStrategy;

  public ProducerStrategy getProducerStrategy(String eventType) {
    if (GENERAL_INFO.getValue().equals(eventType)) {
      return generalInfoProducerStrategy;
    } else if (CMS_INFO.getValue().equals(eventType)) {
      return cmsInfoProducerStrategy;
    } else if (CMS_V2_INFO.getValue().equals(eventType)) {
      return cmsV2InfoProducerStrategy;
    }
    throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
  }
}
