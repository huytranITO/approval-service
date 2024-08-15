package com.msb.bpm.approval.appr.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCheckBlackListPPersonRequest {

  private AuthenInfo authenInfo;
  private String identityCard;
  private String name;
  private String birthday;
  private String endDate;
}
