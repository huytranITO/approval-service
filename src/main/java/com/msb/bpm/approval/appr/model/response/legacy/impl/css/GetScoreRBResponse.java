package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.enums.css.CSSResponseCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetScoreRBResponse {

  private Response response;

  @JsonProperty("RB")
  private GetScoreRBResponseData rbResponse;

  @JsonProperty("EB")
  private GetScoreRBResponseData ebResponse;

  public boolean isSuccess() {
    return CSSResponseCode.isCode(response.getCode(), CSSResponseCode.SUCCESS);
  }
}
