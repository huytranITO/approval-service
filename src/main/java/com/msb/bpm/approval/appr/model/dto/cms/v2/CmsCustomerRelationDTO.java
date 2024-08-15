package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CmsCustomerRelationDTO extends CustomerRelationDTO implements Serializable {
  @NotBlank
  @Size(max = 10)
  @CategoryConstraint(category = ConfigurationCategory.RELATIONSHIP)
  private String relationship;

  // Ho va ten
  @NotBlank
  @Size(max = 100)
  private String fullName;

  // Gioi tinh
  @NotBlank
  @Size(max = 1)
  @CategoryConstraint(category = ConfigurationCategory.GENDER)
  private String gender;

  // Ngay sinh
  @NotNull
  private LocalDate dateOfBirth;

  // Tinh trang hon nhan
  @NotNull
  @Digits(integer = 1, fraction = 0)
  private Integer martialStatus;

  // Quoc tich
  @NotBlank
  @Size(max = 6)
  @JsonProperty("national")
  @CategoryConstraint(category = ConfigurationCategory.NATIONAL)
  private String nation;

  @NotBlank
  @Size(max = 10)
  private String phoneNumber;

  @Email
  @Size(max = 50)
  private String email;
}
