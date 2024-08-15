package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.validator.CustomValidationAddressValue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
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
public class CmsAddressDTO {

  @NotBlank
  @Size(max = 6)
  private String cityCode;

  @NotBlank
  @Size(max = 6)
  private String districtCode;

  @NotBlank
  @Size(max = 6)
  private String wardCode;

  @Size(max = 255)
  private String addressLine;
}

