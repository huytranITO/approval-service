package com.msb.bpm.approval.appr.controller.internal;

import com.msb.bpm.approval.appr.controller.api.CamundaApi;
import com.msb.bpm.approval.appr.model.request.camunda.ProcessInstanceRequest;
import com.msb.bpm.approval.appr.service.camunda.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 15/7/2023, Saturday
 **/
@RestController
@RequiredArgsConstructor
public class CamundaController implements CamundaApi {

  private final ProcessInstanceService processInstanceService;

  @Override
  public void getSubProcessInstance(ProcessInstanceRequest request)
      throws ApiException {
    processInstanceService.updateSubProcessInstance(request);
  }

}
