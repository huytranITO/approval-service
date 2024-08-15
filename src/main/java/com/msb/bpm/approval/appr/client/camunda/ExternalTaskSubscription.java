package com.msb.bpm.approval.appr.client.camunda;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 14/7/2023, Friday
 **/
@Getter
@Setter
public class ExternalTaskSubscription {

  private String processDefinitionKey;
  private boolean includeExtensionProperties;
}
