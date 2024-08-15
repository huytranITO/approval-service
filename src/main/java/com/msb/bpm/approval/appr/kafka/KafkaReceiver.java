package com.msb.bpm.approval.appr.kafka;

import com.msb.bpm.approval.appr.service.formtemplate.impl.FormTemplateServiceImpl;
import com.msb.bpm.approval.appr.service.intergated.impl.BpmOperationServiceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaReceiver {

  private final BpmOperationServiceServiceImpl bpmOperationServiceService;

  private final FormTemplateServiceImpl formTemplateService;
  

  @KafkaListener(
          topics = "${approval-service.kafka.topic.bpm-ops-credit-info.topic-name}",
          groupId = "${spring.kafka.consumer.group-id-bpm-ops}",
          containerFactory = "bpmOperationKafkaListenerContainerFactory"
  )
  public void consumeUpdateCreditInfoStatus(String creditInfo){
    log.info("Consume update credit info: [{}]", creditInfo);
    bpmOperationServiceService.updateStatusCredit(creditInfo);
  }
  @KafkaListener(
      topics = "${approval-service.kafka.topic.template-generator.topic-name}",
      groupId = "${spring.kafka.consumer.group-id}",
      containerFactory = "bpmOperationKafkaListenerContainerFactory"
  )
  public void consumeCreateTemplateResult(String result) {
    log.info("Consume create template result: [{}]", result);
    formTemplateService.updateTemplateChecklist(result);

  }
}
