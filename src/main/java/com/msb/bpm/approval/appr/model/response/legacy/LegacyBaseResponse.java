package com.msb.bpm.approval.appr.model.response.legacy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class  LegacyBaseResponse<T extends LegacyRespDomain> {

  private T respDomain;

  private LegacyRespMessage respMessage;
}
