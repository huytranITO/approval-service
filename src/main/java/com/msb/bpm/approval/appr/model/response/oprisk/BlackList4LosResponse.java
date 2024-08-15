package com.msb.bpm.approval.appr.model.response.oprisk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackList4LosResponse {
  private String classification;
  private String dateAdded;
  private String division;
  private String endDate;
  private String errorMess;
  private String id;
  private String identificationInformation;
  private String inputUser;
  private String listedReasons;
  private String owner;
  private Boolean result;
  private String propertyDescription;
  private String typeOfProperty;
  private String version;
  private String cId;
  @JsonProperty("iDcard_passport")
  private String idCardPassport;
  @JsonProperty("iDcard_passportBusinessRegistration")
  private String idCardPassportBusinessRegistration;
}
