package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SubCreditCardDTO {

  private Long id;

  @NotBlank
  @Size(max = 100)
  private String cardOwnerName;

  @NotBlank
  @Email
  @Size(max = 50)
  private String email;

  @NotBlank
  @Size(max = 20)
  private String phoneNumber;

  @NotNull
  private BigDecimal cardLimitAmount;
}
