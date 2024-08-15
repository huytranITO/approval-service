package com.msb.bpm.approval.appr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class IndividualCustomerEntity extends BaseEntity {

  @Column(name = "first_name", table = "individual_customer")
  private String firstName;

  @Column(name = "last_name", table = "individual_customer")
  private String lastName;

  @Column(name = "gender", table = "individual_customer")
  private String gender;

  @Column(name = "gender_value", table = "individual_customer")
  private String genderValue;

  @Column(name = "date_of_birth", table = "individual_customer")
  private LocalDate dateOfBirth;

  @Column(name = "age", table = "individual_customer")
  private Integer age;

  @Column(name = "marital_status", table = "individual_customer")
  private String martialStatus;

  @Column(name = "marital_status_value", table = "individual_customer")
  private String martialStatusValue;

  @Column(name = "nation", table = "individual_customer")
  private String nation;

  @Column(name = "nation_value", table = "individual_customer")
  private String nationValue;

  @Column(name = "subject", table = "individual_customer")
  private String subject;

  @Column(name = "msb_member", table = "individual_customer")
  private boolean msbMember;

  @Column(name = "employee_code", table = "individual_customer")
  private String employeeCode;

  @Column(name = "email", table = "individual_customer")
  private String email;

  @Column(name = "phone_number", table = "individual_customer")
  private String phoneNumber;

  @Column(name = "literacy", table = "individual_customer")
  private String literacy;

  @Column(name = "literacy_txt", table = "individual_customer")
  private String literacyTxt;

  public String fullName() {
    return lastName + " " + firstName;
  }
}
