package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@SuperBuilder
public class AddressDTO {

  @NotBlank
  @Size(max = 10)
  private String cityCode;

  @Size(max = 100)
  private String cityValue;

  @NotBlank
  @Size(max = 10)
  private String districtCode;

  @Size(max = 100)
  private String districtValue;

  @NotBlank
  @Size(max = 10)
  private String wardCode;

  @Size(max = 100)
  private String wardValue;

//  @NotBlank
  @Size(max = 255)
  private String addressLine;

  private String addressLinkId;
}

