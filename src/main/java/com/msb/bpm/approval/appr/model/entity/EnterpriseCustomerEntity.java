package com.msb.bpm.approval.appr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class EnterpriseCustomerEntity extends BaseEntity {

  @Column(name = "business_type", table = "enterprise_customer")
  private String businessType;

  @Column(name = "business_registration_number", table = "enterprise_customer")
  private String businessRegistrationNumber;

  @Column(name = "company_name", table = "enterprise_customer")
  private String companyName;

  @Column(name = "first_registration_at", table = "enterprise_customer")
  private LocalDate firstRegistrationAt;

  @Column(name = "deputy", table = "enterprise_customer")
  private String deputy;

  @Transient
  private String issuedBy;
  @Transient
  private LocalDate issuedAt;
  @Transient
  private String documentType;
  @Transient
  private String documentTypeValue;
  @Transient
  private String issuedPlace;
  @Transient
  private String issuedPlaceValue;
}
