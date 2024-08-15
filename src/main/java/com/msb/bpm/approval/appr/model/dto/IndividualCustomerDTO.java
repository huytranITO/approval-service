package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.time.LocalDate;
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

@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "employeeCode",
        fieldDependOns = "msbMember"
    )
})
public class IndividualCustomerDTO extends CustomerDTO {

  @JsonProperty(access = Access.WRITE_ONLY)
  private String firstName;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String lastName;

  // Ho va ten
  @NotBlank
  @JsonProperty(value = "name")
  @Size(max = 255)
  private String fullName;

  // Gioi tinh
  @NotBlank
  @Size(max = 10)
  private String gender;

  @Size(max = 30)
  private String genderValue;

  // Ngay sinh
  @NotNull(message = "birthday must not be blank")
  @JsonProperty(value = "birthday")
  private LocalDate dateOfBirth;

  // Tuoi
  @NotNull
  private Integer age;

  // Tinh trang hon nhan
  @NotBlank
  @Size(max = 10)
  @JsonProperty("maritalStatus")
  private String martialStatus;

  @Size(max = 50)
  @JsonProperty("maritalStatusValue")
  private String martialStatusValue;

  // Quoc tich
  @NotBlank(message = "national must not be blank")
  @Size(max = 4)
  @JsonProperty("national")
  private String nation;

  @Size(max = 50)
  @JsonProperty("nationalValue")
  private String nationValue;

  // Doi tuong khach hang
  @JsonProperty(value = "customerSegment")
  @Size(max = 10)
  @NotBlank(message = "customerSegment must not be blank")
  private String subject;

  // La CBNV MSB
  private boolean msbMember = false;

  // Ma nhan vien
  @JsonProperty(value = "staffId")
  @Size(max = 6)
  private String employeeCode;

  @Email
  @NotBlank
  @Size(max = 50)
  private String email;

  @NotBlank
  @Size(max = 20)
  private String phoneNumber;

  @Size(max = 20)
  private String literacy;

  @Size(max = 100)
  @JsonProperty("literacyValue")
  private String literacyTxt;
}
