package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
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
public class CmsCreditLoanDTO extends CmsBaseCreditDTO implements Serializable {

  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 15, fraction = 0)
  private BigDecimal totalCapital;

  private Integer loanPeriod;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.LOAN_PURPOSE)
  private String loanPurpose;

  @NotBlank
  @Size(max = 4)
  @CategoryConstraint(category = ConfigurationCategory.CREDIT_FORM)
  private String creditForm;
}
