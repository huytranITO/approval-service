package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.BranchConstraint;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.validator.CustomValidationAddressValue;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 16/8/2023, Wednesday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@CustomValidationAddressValue.List({
    @CustomValidationAddressValue(
        code = "cityCode"
    ),
    @CustomValidationAddressValue(
        parentCode = "cityCode",
        code = "districtCode"
    ),
    @CustomValidationAddressValue(
        parentCode = "districtCode",
        code = "wardCode"
    )
})
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "deductAccountNumber",
        fieldDependOns = "autoDeductRate"
    ),
    @CustomValidationFieldDependOn(
        field = "subCard",
        message = "{custom.validation.constraints.SubCardMax.message}"
    ),
    @CustomValidationFieldDependOn(
        field = "branchReceive",
        fieldDependOns = "cardReceiveAddress"
    ),
})
public class CmsCreditCardDTO extends CmsBaseCreditDTO implements Serializable {

  @Size(max = 50)
  @CategoryConstraint(category = ConfigurationCategory.CARD_TYPE)
  private String cardType;

  @Size(max = 255)
  private String cardName;

  @Size(max = 255)
  private String secretQuestion;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.AUTO_DEDUCT_RATE)
  private String autoDeductRate;

  @Size(max = 30)
  private String deductAccountNumber;

  @Size(max = 50)
  @Email
  private String email;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.CARD_FORM)
  private String cardForm;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.CARD_RECEIVE_ADDRESS)
  private String cardReceiveAddress;

  @Size(max = 6)
  private String cityCode;

  @Size(max = 6)
  private String districtCode;

  @Size(max = 6)
  private String wardCode;

  @Size(max = 255)
  private String addressLine;

  @Valid
  private List<CmsSubCardDTO> subCard;

  @BranchConstraint(category = ConfigurationCategory.BRANCH_RECEIVE)
  @Size(max = 20)
  private String branchReceive;
}
