package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

