package com.msb.bpm.approval.appr.model.request.insurance.mq;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceMessage {

  private Data data;
  private String source;
  private String userName;
  private String status;
  private String refId;
  private List<String> regulatoryCode = new ArrayList<>();
  private LocalDateTime responseDate;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  public static class Data {
    private String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_FORMAT)
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String email;
    private String identifierNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_FORMAT)
    private LocalDate issuedAt;
    private String issuedPlace;
    private String cityCode;
    private String districtCode;
    private String wardCode;
    private String addressLine;
    private BigDecimal recognizedIncome;
    private Integer martialStatus;
    private String bpmId;
    private String insuranceLead;
    private boolean insuranceStatus;
    private boolean rmStatus;
    private String rmEmployee;
  }


}
