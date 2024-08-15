package com.msb.bpm.approval.appr.model.response.creditconditions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class CreditConditionResponse {

  private Long id;
  private String detail;
  private String controlLevel;
  private String bpmCusId;
  private String state;
  private String objectApply;
  private String timeControl;
  private String creditConditionGroup;
  private String conditionGroupCode;
  private Long policyConditionId;
  private String source;
  private Boolean flag;
  private String timeControlDisburse;
  private Date createDate;
  private Date updateDate;
  private List<CreditConditionParamsResponse> creditConditionParams = new ArrayList<>();
}
