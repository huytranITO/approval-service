package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import static com.msb.bpm.approval.appr.constant.Constant.RegexPattern.FULL_NAME_REGEX;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTemplateSubCreditCardDTO {

  private Long id;

  private String cardOwnerName;

  private String email;

  private String phoneNumber;

  private String cardLimitAmount;

  private String orderDisplay;
}
