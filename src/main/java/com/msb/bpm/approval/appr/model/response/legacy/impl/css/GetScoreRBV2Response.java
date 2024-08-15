package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GetScoreRBV2Response {
  private Response response;

  @JsonProperty("RB")
  private RbResponse rbResponse;
}
