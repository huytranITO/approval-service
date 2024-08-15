package com.msb.bpm.approval.appr.model.dto.cms;

import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_AT;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_BY;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_PLACE;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.PRIORITY;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(field = ISSUED_AT, fieldDependOns = {PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_BY, fieldDependOns = {PRIORITY}),
    @CustomValidationFieldDependOn(field = ISSUED_PLACE, fieldDependOns = {PRIORITY, ISSUED_BY})
})
public class CustomerIdentityDTO {

  private boolean priority = false;

  @NotBlank
  @Size(max = 10)
  private String documentType;

  @NotBlank
  @Size(max = 16)
  private String identifierNumber;

  @NotNull
  private LocalDate issuedAt;

  @NotBlank
  @Size(max = 4)
  private String issuedBy;

  @Size(max = 6)
  private String issuedPlace;
}