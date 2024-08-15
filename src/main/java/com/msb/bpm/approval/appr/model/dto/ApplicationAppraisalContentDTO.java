package com.msb.bpm.approval.appr.model.dto;


import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "authorization",
        fieldDependOns = {"contentType", "detail"}
    )
})
public class ApplicationAppraisalContentDTO {

  private Long id;

  @NotBlank
  private String contentType;

  @Size(max = 10)
  @NotBlank
  private String criteriaGroup;

  @Size(max = 100)
  private String criteriaGroupValue;

  @Size(max = 500)
  @NotEmpty
  private Set<String> detail;

  private Set<String> detailValue;

  @Size(max = 250)
  @NotBlank
  private String regulation;

  @Size(max = 1000)
  @NotBlank
  private String managementMeasures;

  @Size(max = 45)
  private String authorization;

  private Integer orderDisplay;
}
