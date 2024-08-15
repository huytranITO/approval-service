package com.msb.bpm.approval.appr.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class AddressEntity extends BaseEntity {

  @Basic
  @Column(name = "city_code")
  private String cityCode;

  @Basic
  @Column(name = "city_value")
  private String cityValue;

  @Basic
  @Column(name = "district_code")
  private String districtCode;

  @Basic
  @Column(name = "district_value")
  private String districtValue;

  @Basic
  @Column(name = "ward_code")
  private String wardCode;

  @Basic
  @Column(name = "ward_value")
  private String wardValue;

  @Basic
  @Column(name = "address_line")
  private String addressLine;
}
