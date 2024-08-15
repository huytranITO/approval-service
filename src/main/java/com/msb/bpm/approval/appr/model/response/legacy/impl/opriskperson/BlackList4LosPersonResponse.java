package com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackList4LosPersonResponse {

  private String address;
  private String branchInput;
  private String cusId;
  private String dateEx;
  private String dateInput;
  private String dob;
  private String dscn;
  private String dtcn;
  private String errorMess;
  private Long id;
  private String inputUser;
  private String name;
  private Long recordId;
  private Boolean result;
  private Long version;
}
