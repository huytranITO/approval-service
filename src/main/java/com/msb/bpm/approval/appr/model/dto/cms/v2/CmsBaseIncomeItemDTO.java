package com.msb.bpm.approval.appr.model.dto.cms.v2;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.OTHER;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.RENTAL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;
import static com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity_.INCOME_OWNER;
import static com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity_.INCOME_OWNER_NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
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
@JsonTypeInfo(use = Id.NAME, visible = true, include = As.EXISTING_PROPERTY, property = "incomeType", defaultImpl = CmsDefaultIncomeItemDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CmsSalaryDTO.class, name = SALARY),
    @JsonSubTypes.Type(value = CmsRentalIncomeDTO.class, name = RENTAL),
    @JsonSubTypes.Type(value = CmsIndividualBusinessDTO.class, name = INDIVIDUAL_BUSINESS),
    @JsonSubTypes.Type(value = CmsEnterpriseBusinessDTO.class, name = ENTERPRISE_BUSINESS),
    @JsonSubTypes.Type(value = CmsOtherDTO.class, name = OTHER),
    @JsonSubTypes.Type(value = CmsPropertyIncomeDTO.class, name = PROPERTY_BUSINESS)
})
@CustomValidationFieldDependOn(
    field = INCOME_OWNER_NAME,
    fieldDependOns = INCOME_OWNER
)
public abstract class CmsBaseIncomeItemDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String refIncomeItemId;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.INCOME_TYPE)
  private String incomeType;

  @NotBlank
  @Size(max = 100)
  private String refCusId;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.RELATIONSHIP)
  private String incomeOwner;

  @Size(max = 255)
  private String incomeOwnerName;

  private transient Long customerAdditionalId;

  private transient Long customerId;
}
