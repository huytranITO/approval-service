package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
