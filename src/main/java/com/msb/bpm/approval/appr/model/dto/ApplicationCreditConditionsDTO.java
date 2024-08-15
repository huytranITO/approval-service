package com.msb.bpm.approval.appr.model.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationCreditConditionsDTO {

  private Long id;

  private String segment;

  @Size(max = 100)
  @NotBlank
  private String group;

  @Size(max = 100)
  private String groupValue;

  @Size(max = 100)
  @NotBlank
  private String code;

  @Size(max = 100)
  private String codeValue;

  @Size(max = 2000)
  @NotBlank
  private String detail;

  @Size(max = 100)
  @NotBlank
  private String timeOfControl;

  @Size(max = 100)
  private String timeOfControlValue;

  @Size(max = 100)
  @NotBlank
  private String applicableSubject;

  @Size(max = 100)
  private String applicableSubjectValue;

  @Size(max = 100)
  @NotBlank
  private String controlUnit;

  @Size(max = 100)
  private String controlUnitValue;

  @Size(max = 100)
  private String timeControlDisburse;

  private Integer orderDisplay;

  private Long masterId;

  private String bpmCusId;
  private Long creditConditionId;
  private String state;

  @Valid
  private List<CreditConditionParamsDTO> creditConditionParams = new ArrayList<>();
}
