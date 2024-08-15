package com.msb.bpm.approval.appr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "customer_relation_ship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class CustomerRelationShipEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  private CustomerEntity customer;

  @Basic
  @Column(name = "customer_ref_id")
  private Long customerRefId;

  @Basic
  @Column(name = "relationship")
  private String relationship;

  @Basic
  @Column(name = "relationship_value")
  private String relationshipValue;

  @Basic
  @Column(name = "relationship_ref_id")
  private Long relationshipRefId;

  @Basic
  @Column(name = "payment_guarantee")
  private Boolean paymentGuarantee;
}
