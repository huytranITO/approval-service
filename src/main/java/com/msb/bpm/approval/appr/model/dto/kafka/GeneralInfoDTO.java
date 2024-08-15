package com.msb.bpm.approval.appr.model.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.application.ExpirationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GeneralInfoDTO {

  private String requestCode;

  private String processInstanceId;

  private String businessName;

  private String cif;

  private String customerName;

  private String businessType;

  private String taskDefinitionKey;

  private String status;

  private String username;

  private String createdBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp modifiedAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp updatedAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp dueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp firstSubmitTime;

  private String modifiedBy;

  private Integer isDeleted = 0;

  private String bpmTaskId;

  private String props;

  private String loanFormal;

  private String formCase;

  private String customerType;

  private String processDefinitionId;

  private String identityId;

  private String creatorName;

  private String fullName = "rm1";

  private String fullNameOfUsername;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp startDate;

  private String loanProductCode;

  private String assignee;

  private String loanAmount = "rm1";

  private String rlosCode;

  private ExpirationStatus expirationStatus;

  private Integer numberOfDayOff;

  private String ecmSyncStatus;

  private Integer slaDuration;

  private Integer numberOfRollback;

  private Long latestPihId;

  private String commentMessage;

  private String fieldAllowNull;

  private String source;

  private String refId;

  private String customerPhoneNumber;

  private String phoneNumber;

  private String customerSourceCode;

  private String businessUnitCode;

  private String branch;

  private String segment;

  private String creditType;

  private String productCode;

  private String specializedBank;

  private String taskDefinitionKeyName;

  private ProcessPropsDTO processProps;
}
