package com.msb.bpm.approval.appr.model.response.bpm.operation;

import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AsyncBpmStatusCredit implements Serializable {

  private static final long serialVersionUID = 2806496721866403774L;

  private String approvalBpmId;
  private Long approvalLoanId;
  private String accountNo;
  private String acfNo;
  private String opsBpmId;
  private String status;
}