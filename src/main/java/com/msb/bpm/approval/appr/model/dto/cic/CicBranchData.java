package com.msb.bpm.approval.appr.model.dto.cic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CicBranchData {

  private String bpmDealingRoomCode;

  private String bpmDealingRoomName;

  private String cicBranchCode;

  private String cicBranchName;

  private String cicDealingRoomCode;

}
