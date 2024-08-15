package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_AT;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_BY;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_PLACE;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.PRIORITY;

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
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
@ToString
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(message = "{javax.validation.constraints.NotNull.message}", field = ISSUED_AT, fieldDependOns = {
        PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_BY, fieldDependOns = {PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_PLACE, fieldDependOns = {PRIORITY, ISSUED_BY})
})
public class CustomerIdentityDTO {

  private Long id;

  private Long refIdentityId;

  private boolean priority = false;

  @NotBlank
  @JsonProperty(value = "type")
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.DOCUMENT_TYPE)
  private String documentType;

  @Size(max = 50)
  private String documentTypeValue;

  @NotBlank
  @JsonProperty(value = "identityNumber")
  @Size(max = 100)
  private String identifierCode;

  @JsonProperty(value = "issuedDate")
  private LocalDate issuedAt;

  @Size(max = 10)
  private String issuedBy;

  @Size(max = 50)
  private String issuedByValue;

  @Size(max = 10)
  private String issuedPlace;

  @Size(max = 500)
  private String issuedPlaceValue;

  private Integer orderDisplay;

  private String ldpIdentityId;
}
