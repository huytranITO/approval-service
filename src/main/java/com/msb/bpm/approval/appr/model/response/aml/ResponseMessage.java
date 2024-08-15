package com.msb.bpm.approval.appr.model.response.aml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseMessage {

  @JsonProperty("respCode")
  private String responseCode;

  @JsonProperty("respDesc")
  private String responseDescription;

}
