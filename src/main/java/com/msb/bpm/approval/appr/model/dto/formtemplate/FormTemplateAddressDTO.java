package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateAddressDTO {

  @NotBlank
  @Size(max = 10)
  private String salaryCityCode = "null";

  @Size(max = 50)
  private String salaryCityValue = "null";

  @NotBlank
  @Size(max = 10)
  private String salaryDistrictCode = "null";

  @Size(max = 50)
  private String salaryDistrictValue = "null";

  @NotBlank
  @Size(max = 10)
  private String salaryWardCode = "null";

  @Size(max = 50)
  private String salaryWardValue = "null";

  //  @NotBlank
  @Size(max = 255)
  private String salaryAddressLine = "null";


  @NotBlank
  @Size(max = 10)
  private String companyCityCode = "null";

  @Size(max = 50)
  private String companyCityValue = "null";

  @NotBlank
  @Size(max = 10)
  private String companyDistrictCode = "null";

  @Size(max = 50)
  private String companyDistrictValue = "null";

  @NotBlank
  @Size(max = 10)
  private String companyWardCode = "null";

  @Size(max = 50)
  private String companyWardValue = "null";

  //  @NotBlank
  @Size(max = 255)
  private String companyAddressLine = "null";


  @NotBlank
  @Size(max = 10)
  private String businessCityCode = "null";

  @Size(max = 50)
  private String businessCityValue = "null";

  @NotBlank
  @Size(max = 10)
  private String businessDistrictCode = "null";

  @Size(max = 50)
  private String businessDistrictValue = "null";

  @NotBlank
  @Size(max = 10)
  private String businessWardCode = "null";

  @Size(max = 50)
  private String businessWardValue = "null";

  //  @NotBlank
  @Size(max = 255)
  private String businessAddressLine = "null";
}
