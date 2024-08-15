package com.msb.bpm.approval.appr.model.dto.cms.v2;

import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_AT;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_BY;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_PLACE;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.PRIORITY;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.time.LocalDate;
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
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(field = ISSUED_AT, fieldDependOns = {PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_BY, fieldDependOns = {PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_PLACE, fieldDependOns = {PRIORITY, ISSUED_BY})
})
public class CmsCustomerIdentityDTO {

  private boolean priority = false;

  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.DOCUMENT_TYPE)
  private String documentType;

  @NotBlank
  @Size(max = 16)
  private String identifierNumber;

  private LocalDate issuedAt;

  @Size(max = 4)
  @CategoryConstraint(category = ConfigurationCategory.ISSUE_BY)
  private String issuedBy;

  @Size(max = 6)
  private String issuedPlace;

  @Size(max = 100)
  @NotBlank
  @JsonProperty("refIdentityId")
  private String ldpIdentityId;
}