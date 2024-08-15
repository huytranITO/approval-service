package com.msb.bpm.approval.appr.model.response.creditconditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreditConditionParamsResponse {

  private String parameter;

  private String dataType;

  private Integer length;

  private String content;

  private String value;
}
