package com.msb.bpm.approval.appr.model.response.customer;

import lombok.Data;

@Data
public class CustomerDetailResponse {

  private Long id;
  private String bpmCif;
  private String name;
  private String birthday;
  private String gender;
  private String national;
  private String maritalStatus;
  private String phoneNumber;
  private String email;
  private String customerSegment;
  private String staffId;
  private Integer version;
  private boolean active;

}
