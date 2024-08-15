package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormTemplateApplicationRepaymentDTO {

  @NotNull private String totalRepay;

  private String dti;

  private String dsr;

  private String mue;
  private String ltv = "null";

  @NotBlank
  @Size(max = 2000)
  private String evaluate;
}
