package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CustomerContactDTO {
  @NotBlank
  @Size(max = 10)
  private String contactType;

  @NotBlank
  @Size(max = 50)
  private String value;
}
