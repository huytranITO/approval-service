package com.msb.bpm.approval.appr.model.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "customer_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class CustomerAddressEntity extends AddressEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  private CustomerEntity customer;

  private String ldpAddressId;

  @Basic
  @Column(name = "ref_address_id")
  private Long refAddressId;

  @Basic
  @Column(name = "address_type")
  private String addressType;

  @Basic
  @Column(name = "address_type_value")
  private String addressTypeValue;

  @Basic
  @Column(name = "same_permanent_residence")
  private boolean samePermanentResidence;

  @Basic
  @Column(name = "can_delete")
  private boolean canDelete;

  @Basic
  @Column(name = "address_link_id")
  private String addressLinkId;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}
