package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 16/8/2023, Wednesday
 **/
@Getter
@Setter
@ToString
@JsonTypeInfo(use = Id.NAME, visible = true, include = As.EXISTING_PROPERTY, property = "creditType", defaultImpl = CmsDefaultCreditDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CmsCreditLoanDTO.class, name = "V001"),
    @JsonSubTypes.Type(value = CmsCreditOverdraftDTO.class, name = "V002"),
    @JsonSubTypes.Type(value = CmsCreditCardDTO.class, name = "V003")
})
@CustomValidationFieldDependOn(
    field = "guaranteeForm",
    fieldDependOns = "creditType"
)
public abstract class CmsBaseCreditDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String refCreditId;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.CREDIT_TYPE)
  private String creditType;

//  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.GUARANTEE_FORM)
  private String guaranteeForm;

  @NotNull
  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 15, fraction = 0)
  private BigDecimal loanAmount;
}
