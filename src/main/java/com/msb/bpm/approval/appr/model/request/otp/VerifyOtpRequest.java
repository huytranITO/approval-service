package com.msb.bpm.approval.appr.model.request.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

  private Integer ivalueTime;

  private String otp;

  private String otpId;

  private String transactionId;
}