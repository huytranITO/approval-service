package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
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
public class CmsCreditOverdraftDTO extends CmsBaseCreditDTO implements Serializable {

  private Integer limitSustentivePeriod;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.LOAN_PURPOSE)
  private String loanPurpose;
}
