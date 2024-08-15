package com.msb.bpm.approval.appr.model.dto;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtherIncomeDTO extends BaseIncomeDTO {

  private boolean incomeFuture;

  @Size(max = 250)
  private String incomeInfo;

  @Size(max = 10)
  private String incomeDetail;

  @Size(max = 100)
  private String incomeDetailValue;

  @Size(max = 2000)
  private String explanation;

  private String ldpOtherId;
}
