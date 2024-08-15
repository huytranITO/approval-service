package com.msb.bpm.approval.appr.handler.camunda;

import static com.msb.bpm.approval.appr.enums.camunda.CamundaVariable.TRANSFER_TO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 14/5/2023, Sunday
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ExternalTaskSubscription("CHECK_WORKFLOW")
public class ApplicationWorkFlowHandler implements ExternalTaskHandler {
  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    try {
      VariableMap map = Variables.createVariables();
      map.put(TRANSFER_TO.getValue(), "BO/SBO");
      externalTaskService.complete(externalTask, map);
      log.info(
          "Handler task, separate workflow with process instance id {} and business key {} success......",
          externalTask.getProcessInstanceId(), externalTask.getBusinessKey());
    } catch (Throwable e) {
      log.error(
          "Handler task, separate workflow with process instance id {} and business key {} failure, ",
          externalTask.getProcessInstanceId(), externalTask.getBusinessKey(), e);
      externalTaskService.handleFailure(externalTask, "Handler task, separate workflow failure",
          e.getMessage(), 3, 60000);
    }
  }
}
