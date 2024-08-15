package com.msb.bpm.approval.appr.model.response.cic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CICDataResponse {
  @JsonProperty("CustomerId")
  private Long customerId;
  private Long refCustomerId;
  private String relationship;
  private Integer status;
  private String identifierCode;
  private String cicCode;
  private LocalDateTime queryAt;
  private Long clientQuestionId;
  private String subject;
  private String customerType;
  private String customerName;
  private String productCode;
  private String errorCodeSBV;
}