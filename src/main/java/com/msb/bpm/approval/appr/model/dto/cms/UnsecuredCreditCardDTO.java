package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsecuredCreditCardDTO extends ApplicationCreditCommonDTO {
  @Size(max = 10)
  private String cardType;

  @Size(max = 255)
  private String cardName;

  @Size(max = 255)
  private String secretQuestion;

  @Size(max = 30)
  private String deductAccountNumber;

  @Size(max = 10)
  private String autoDeductRate;

  @Email
  @Size(max = 50)
  private String email;

  private Integer limitSustentivePeriod;
}