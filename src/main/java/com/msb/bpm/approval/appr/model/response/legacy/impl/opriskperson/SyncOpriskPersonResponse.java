package com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson;

import com.msb.bpm.approval.appr.model.response.legacy.LegacyBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncOpriskPersonResponse {

  private LegacyBaseResponse<CheckBlackListPersonResDomain> checkBlackListP;

}
