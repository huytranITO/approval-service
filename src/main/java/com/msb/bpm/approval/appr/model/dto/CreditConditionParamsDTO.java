package com.msb.bpm.approval.appr.model.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditConditionParamsDTO {

  @NotBlank
  private String parameter;

  @NotBlank
  private String dataType;

  private Integer length;

  private String content;

  @NotBlank
  private String value;
}
