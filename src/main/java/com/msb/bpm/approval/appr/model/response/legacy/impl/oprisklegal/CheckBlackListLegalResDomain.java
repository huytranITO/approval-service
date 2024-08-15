package com.msb.bpm.approval.appr.model.response.legacy.impl.oprisklegal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyRespDomain;
import lombok.Data;

@Data
public class CheckBlackListLegalResDomain extends LegacyRespDomain {

  @JsonProperty("BlackList4LosO")
  private BlackList4LosLegalResponse blackList4LosO;
}
