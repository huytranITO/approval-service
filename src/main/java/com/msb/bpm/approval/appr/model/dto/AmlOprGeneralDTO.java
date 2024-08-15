package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmlOprGeneralDTO {

  private String subject;
  private String identifierCode;
  private String amlResult;
  private String oprResult;
}
