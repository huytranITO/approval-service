package com.msb.bpm.approval.appr.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCheckBlackListPLegalRequest {

  private AuthenInfo authenInfo;
  private String businessRegistrationNumber;
  private String companyName;
  private String businessRegistrationDate;
  private Date endDate;

  @JsonProperty("iDcard_passportOfTheRepresentative")
  private String iDcardPassportOfTheRepresentative;
}
