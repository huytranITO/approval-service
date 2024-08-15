package com.msb.bpm.approval.appr.client.customer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/11/2023, Monday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SuperBuilder
public class CustomerVersionRequest {
  private Long refCustomerId;
  private String bpmCif;
  @JsonProperty("cif")
  private String cifNo;
  private String name;
  private String identityNumber;
  private String issuedBy;
  private String issuedPlace;
  private LocalDate issuedDate;
  private LocalDate birthday;
  private String identityType;
  private String gender;
  private String national;
  private Integer maritalStatus;
  private String phoneNumber;
  private String email;
  private String customerSegment;
  private Boolean object;
  private String staffId;
}
