package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.ALWAYS)
public class ApplicationHistoricIntegrationDTO {

  private Long id;

  private String bpmId;

  private String fullName;

  private String cif;

  private String identifierCode;

  private BigDecimal loanAmount;

  private String documentCode;

  private String createdBy;

  private String integratedStatus;

  private String businessUnit;

  private Long applicationCreditId;

  private String creditType;

  private String guaranteeForm;

  private String integratedSystem;

  private String integratedStatusDetail;

  private String errorDescription;

  private String errorCode;

  private String updatedBy;

  private LocalDateTime updatedAt;

  private Boolean retry;

  private LocalDateTime createdAt;
}
