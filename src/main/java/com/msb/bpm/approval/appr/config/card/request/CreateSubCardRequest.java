package com.msb.bpm.approval.appr.config.card.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSubCardRequest {
  private Integer cifNumber;

  private String issueContract;

  private String fullName;

  private String firstName;

  private String lastName;

  private BigDecimal amount;

  private String address;

  private String branchCode;

  private String type;

  private String channel;
}
