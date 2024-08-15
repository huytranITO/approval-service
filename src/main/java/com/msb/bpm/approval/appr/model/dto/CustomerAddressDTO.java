package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@ToString
@SuperBuilder
public class CustomerAddressDTO extends AddressDTO {

  private Long id;

  private Long refAddressId;

  @JsonProperty(access = Access.READ_ONLY)
  private boolean canDelete = false;

  @NotBlank
  @Size(max = 10)
  private String addressType;

  @Size(max = 100)
  private String addressTypeValue;

  @JsonProperty(value = "hktt")
  private boolean samePermanentResidence = false;

  private Integer orderDisplay;

  private String ldpAddressId;
}
