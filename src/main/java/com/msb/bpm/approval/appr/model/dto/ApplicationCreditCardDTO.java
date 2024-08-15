package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.Constant.RegexPattern.FULL_NAME_REGEX;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@NoArgsConstructor
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "subCreditCards",
        fieldNeedValidate = "cardLimitAmount",
        fieldDependOns = {"hasSubCard", "loanAmount"}
    ),
    @CustomValidationFieldDependOn(
        field = "cardReceiveAddress",
        fieldDependOns = "cardForm"
    ),
    @CustomValidationFieldDependOn(
        message = "{javax.validation.constraints.NotNull.message}",
        field = "address",
        fieldDependOns = {"cardForm", "cardReceiveAddress"}
    ),
    @CustomValidationFieldDependOn(
        field = "deductAccountNumber",
        fieldDependOns = "autoDeductRate"
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
  @Size(max = 100)
  private String cardType;

  @Size(max = 100)
  private String cardTypeValue;

  @NotBlank
  @Size(max = 50)
  private String way4CardCode;

  @Size(max = 50)
  private String way4BranchCode;

  @NotBlank
  @Size(max = 250)
  @Pattern(regexp = FULL_NAME_REGEX)
  private String cardName;

  @NotBlank
  @Size(max = 250)
  private String secretQuestion;

  @NotNull
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

//  @NotBlank
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

//  @Valid
  private AddressDTO address;

  private boolean hasSubCard = false;

  // Require khi hasSubCard = true
  @Valid
  private List<SubCreditCardDTO> subCreditCards = new ArrayList<>();

  // Mã HĐ mức Liability của KH
  private String contractL;

  // Mã HĐ mức Issuing của KH
  private String issuingContract;

  // Số thẻ
  private String contractNumber;

  // Ngày phát hành
  private LocalDateTime createdDate;

  public List<SubCreditCardDTO> getSubCreditCards() {
    if (CollectionUtils.isEmpty(subCreditCards)) {
      return subCreditCards;
    }
    return subCreditCards
        .stream()
        .sorted(
            comparing(SubCreditCardDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toList());
  }
}
