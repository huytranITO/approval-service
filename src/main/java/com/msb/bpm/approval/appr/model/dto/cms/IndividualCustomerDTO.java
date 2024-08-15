package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualCustomerDTO extends CustomerDTO {

  // Ho va ten
  @NotBlank
  @Size(max = 100)
  private String fullName;

  // Gioi tinh
  @NotBlank
  @Size(max = 1)
  private String gender;

  // Ngay sinh
  @NotNull
  private LocalDate dateOfBirth;

  // Tinh trang hon nhan
  @NotNull
  private Integer martialStatus;

  // Quoc tich
  @NotBlank
  @Size(max = 6)
  @JsonProperty("national")
  private String nation;

  @NotBlank
  @Size(max = 10)
  private String phoneNumber;

  @Email
  @NotBlank
  @Size(max = 50)
  private String email;
}
