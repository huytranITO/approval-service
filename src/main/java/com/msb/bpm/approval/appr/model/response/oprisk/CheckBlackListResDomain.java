package com.msb.bpm.approval.appr.model.response.oprisk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyRespDomain;
import lombok.Data;

@Data
public class CheckBlackListResDomain extends LegacyRespDomain {
  @JsonProperty("BlackList4LosC")
  private BlackList4LosResponse blackList4LosC;
}
