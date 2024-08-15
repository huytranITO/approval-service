package com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyRespDomain;
import lombok.Data;

@Data
public class CheckBlackListPersonResDomain extends LegacyRespDomain {

  @JsonProperty("BlackList4LosP")
  private BlackList4LosPersonResponse blackList4LosP;
}
