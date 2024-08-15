package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.Constant.RegexPattern.FULL_NAME_REGEX;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

  private String ldpSubId;

  @NotBlank
  @Size(max = 255)
  @Pattern(regexp = FULL_NAME_REGEX)
  private String cardOwnerName;

  @Email
  @Size(max = 50)
  private String email;

  @NotBlank
  @Size(max = 20)
  private String phoneNumber;

  @NotNull
  private BigDecimal cardLimitAmount;

  private Integer orderDisplay;
}
