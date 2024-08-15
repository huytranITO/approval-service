package com.msb.bpm.approval.appr.model.dto.cms.v2;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class CmsIncomeDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String refIncomeId;

  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.INCOME_METHOD)
  private String incomeMethod = ACTUALLY;

  @CategoryConstraint(category = ConfigurationCategory.CONVERSION_METHOD)
  private String conversionMethod;

  @NotNull
  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 15, fraction = 0)
  private BigDecimal recognizedIncome;

  @NotEmpty
  @Valid
  private List<CmsBaseIncomeItemDTO> incomeItems = new ArrayList<>();
}
