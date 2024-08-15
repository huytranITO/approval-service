package com.msb.bpm.approval.appr.model.dto.cms.v2;

import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.BUSINESS_TYPE;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CmsEnterpriseRelationDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String refEnterpriseId;

  @Size(max = 20)
  @CategoryConstraint(category = BUSINESS_TYPE)
  private String businessType;

  @Size(max = 100)
  private String businessRegistrationNumber;

  @Size(max = 255)
  private String companyName;
}
