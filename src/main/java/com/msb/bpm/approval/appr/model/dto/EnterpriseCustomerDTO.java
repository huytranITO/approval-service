package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnterpriseCustomerDTO extends CustomerDTO {

  @NotBlank
  @Size(max = 10)
  private String businessType;

  @NotBlank
  @Size(max = 100)
  private String businessRegistrationNumber;

  @NotBlank
  @Size(max = 255)
  private String companyName;

  private LocalDate firstRegistrationAt;

  @Size(max = 100)
  private String deputy;
}
