package com.msb.bpm.approval.appr.model.request.camunda;

import lombok.Data;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 16/7/2023, Sunday
 **/
@Data
public class ProcessInstanceRequest {
  private String processInstanceId;
  private String processBusinessKey;
  private String processDefinitionKey;
}
