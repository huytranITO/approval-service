package com.msb.bpm.approval.appr.model.request.oprisk;

import com.msb.bpm.approval.appr.model.request.AuthenInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
public class CheckBlackListPLegalData {

  private AuthenInfo authenInfo;
  private String collateralType;
  private String identifiesInfo;
}
