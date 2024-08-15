package com.msb.bpm.approval.appr.config.card.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {
  private String bpmId;

  private Integer cifNumber;

  private String regNumber;

  private String fullName;

  private String firstName;

  private String lastName;

  private String payment;

  private String secretQuestion;

  private String rbsNumber;

  private BigDecimal amount;

  private String address;

  private String productType;

  private String cardType;

  private String policyCode;

  private String liabilityType;

  private String branchCode;

  private String institutionCode;

  private String type;

  private String channel;

  private Integer isChargeFee;
  private Boolean chargeFee;
  private String email;
  private String saleCode;
  private String image;
  private String note;
}
