package com.msb.bpm.approval.appr.model.response.customer;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RBCustomerDetailResponse {
  private Long id;
  private String bpmCif;
  private String name;
  private LocalDate birthday;
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
