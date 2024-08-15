package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.validator.CustomValidationAddressValue;
import java.io.Serializable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
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
public class CmsEnterpriseBusinessDTO extends CmsBaseIncomeItemDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String businessRegistrationNumber;

  @NotBlank
  @Size(max = 255)
  private String companyName;

  @Size(max = 255)
  private String mainBusinessSector;

  @NotBlank
  @Size(max = 10)
  private String cityCode;

  @NotBlank
  @Size(max = 10)
  private String districtCode;

  @NotBlank
  @Size(max = 10)
  private String wardCode;

  @NotBlank
  @Size(max = 255)
  private String addressLine;

  @Digits(integer = 2, fraction = 2)
  private Double capitalContributionRate;

  @Size(max = 2000)
  private String businessScale;

  @Digits(integer = 10, fraction = 0)
  private Integer businessExperience;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.BUSINESS_OWNER_STATUS)
  private String businessOwnerStatus;
}
