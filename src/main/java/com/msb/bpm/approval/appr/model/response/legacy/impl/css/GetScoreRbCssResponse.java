package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.enums.css.CSSResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GetScoreRbCssResponse {

  private GetScoreRBV2Response data;
  public boolean isSuccess() {
    return CSSResponseCode.isCode(data.getResponse().getCode(), CSSResponseCode.SUCCESS);
  }
}
