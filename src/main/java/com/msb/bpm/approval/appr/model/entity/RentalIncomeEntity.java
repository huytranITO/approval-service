package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Entity
@Table(name = "rental_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class RentalIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpRentalId;

  @Basic
  @Column(name = "asset_type")
  private String assetType;

  @Basic
  @Column(name = "asset_type_value")
  private String assetTypeValue;

  @Basic
  @Column(name = "asset_owner")
  private String assetOwner;

  @Basic
  @Column(name = "asset_address")
  private String assetAddress;

  @Basic
  @Column(name = "renter")
  private String renter;

  @Basic
  @Column(name = "renter_phone")
  private String renterPhone;

  @Basic
  @Column(name = "rental_purpose")
  private String rentalPurpose;

  @Basic
  @Column(name = "rental_purpose_value")
  private String rentalPurposeValue;

  @Basic
  @Column(name = "rental_price")
  private BigDecimal rentalPrice;

  @Basic
  @Column(name = "explanation")
  private String explanation;

  @Basic
  @Column(name = "customer_id")
  private Long customerId;

  @Basic
  @Column(name = "ref_customer_id")
  private Long refCustomerId;

  @Basic
  @Column(name = "income_owner")
  private String incomeOwner;

  @Basic
  @Column(name = "income_owner_value")
  private String incomeOwnerValue;

  @Basic
  @Column(name = "income_owner_name")
  private String incomeOwnerName;

  @Basic
  @Column(name = "income_type")
  private String incomeType;

  @Basic
  @Column(name = "income_type_value")
  private String incomeTypeValue;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @ManyToMany(mappedBy = "rentalIncomes", cascade = CascadeType.ALL)
  private Set<ApplicationIncomeEntity> applicationIncomes = new HashSet<>();

  @Transient
  private Long oldId;

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
  @Column(name = "address_link_id")
  private String addressLinkId;
}
