package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn (
        field = "subCreditCards",
        fieldDependOns = "hasSubCard"
    ),
    @CustomValidationFieldDependOn(
        field = "cardReceiveAddress",
        fieldDependOns = "cardForm"
    )
})
public class ApplicationCreditCardDTO extends ApplicationCreditDTO {

  private Long appCreditCardId;

  @NotBlank
  @Size(max = 255)
  private String cardPolicyCode;

  @NotBlank
  @Size(max = 250)
  private String productName;

  @NotBlank
  @Size(max = 10)
  private String cardType;

  @Size(max = 100)
  private String cardTypeValue;

  @NotBlank
  @Size(max = 10)
  private String way4CardCode;

  @NotBlank
  @Size(max = 250)
  private String cardName;

  @Size(max = 250)
  private String secretQuestion;

  private Integer limitSustentivePeriod;

  @NotBlank
  @Email
  @Size(max = 50)
  private String email;

  @NotBlank
  @Size(max = 10)
  private String autoDeductRate;

  @Size(max = 100)
  private String autoDeductRateValue;

  @NotBlank
  @Size(max = 14)
  private String deductAccountNumber;

  @NotBlank
  @Size(max = 10)
  private String cardForm;

  @Size(max = 100)
  private String cardFormValue;

//  @NotBlank
  @Size(max = 10)
  private String cardReceiveAddress;

  @Size(max = 100)
  private String cardReceiveAddressValue;

  private boolean hasSubCard = false;

  // Require khi hasSubCard = true
  @Valid
  private List<SubCreditCardDTO> subCreditCards = new ArrayList<>();
}
