package com.msb.bpm.approval.appr.model.dto.kafka.cms;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CmsLoanApplicationApprovedDTO {

  @Size(max = 10)
  private String creditType;

  @Size(max = 10)
  private String guaranteeForm;

  @Size(max = 10)
  private String loanPurpose;

  @Size(max = 10)
  private String creditForm;

  @Digits(integer = 15, fraction = 0)
  private BigDecimal loanAmount;

  private Integer limitSustentivePeriod;

  private String liabilityContract;

  private String issueContract;

  private LocalDateTime createdDate;

}
