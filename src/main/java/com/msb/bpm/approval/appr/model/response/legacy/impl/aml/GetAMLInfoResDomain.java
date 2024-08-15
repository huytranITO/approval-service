package com.msb.bpm.approval.appr.model.response.legacy.impl.aml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyRespDomain;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAMLInfoResDomain extends LegacyRespDomain {

  @JsonProperty("Item")
  private Item item;

  private String inforError;

}
