package com.msb.bpm.approval.appr.model.response.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditInfoConsumer {
  private String requestCode;
  private Long idLoan;
  private String accountNo;
  private String status;
  private String acfNo;
}