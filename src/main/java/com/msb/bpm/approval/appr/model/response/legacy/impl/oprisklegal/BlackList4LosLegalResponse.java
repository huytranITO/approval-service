package com.msb.bpm.approval.appr.model.response.legacy.impl.oprisklegal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackList4LosLegalResponse {

  private String businessRegistrationNumber;
  private Long cId;
  private String classificationOfObjects;
  private String companyName;
  private String dateAdded;
  private String dateOfBusinessRegistration;
  private String division;
  private String endDate;
  private String errorMess;
  @JsonProperty("iDcard_passportOfTheRepresentative")
  private String iDcardPassportOfTheRepresentative;
  private Long id;
  private String legalRepresentative;
  private String listedReasons;
  private Boolean result;
  private String userInput;
  private Long version;
}
