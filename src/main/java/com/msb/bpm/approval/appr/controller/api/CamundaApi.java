package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.request.camunda.ProcessInstanceRequest;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 15/7/2023, Saturday
 **/
@RequestMapping("/internal/api/v1/camunda")
public interface CamundaApi {

  @GetMapping("/sub-process-instance")
  void getSubProcessInstance(ProcessInstanceRequest request) throws ApiException;

}
