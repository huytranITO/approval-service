package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CmsCustomerAddressDTO extends CmsAddressDTO {

  @NotBlank
  @Size(max = 4)
  @CategoryConstraint(category = ConfigurationCategory.RB_ADDRESS_TYPE_V002)
  private String addressType;

  private String addressLinkId = UUID.randomUUID().toString();

  @Size(max = 100)
  @NotBlank
  @JsonProperty("refAddressId")
  private String ldpAddressId;
}