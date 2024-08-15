package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.validator.CustomValidationAddressValue;
import java.io.Serializable;
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
public class CmsSalaryDTO extends CmsBaseIncomeItemDTO implements Serializable {

  @NotBlank
  @Size(max = 255)
  private String companyName;

  @Size(max = 255)
  private String position;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.PAY_TYPE)
  private String payType;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.LABOR_TYPE)
  private String laborType;

  @Size(max = 10)
  private String cityCode;

  @Size(max = 10)
  private String districtCode;

  @Size(max = 10)
  private String wardCode;

  @Size(max = 255)
  private String addressLine;

  @Size(max = 20)
  private String phoneWork;
}
