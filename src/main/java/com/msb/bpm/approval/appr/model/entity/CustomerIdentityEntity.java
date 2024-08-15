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
import java.time.LocalDate;

@Entity
@Table(name = "customer_identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class CustomerIdentityEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  private CustomerEntity customer;

  private String ldpIdentityId;

  @Basic
  @Column(name = "ref_identity_id")
  private Long refIdentityId;

  @Basic
  @Column(name = "document_type")
  private String documentType;

  @Basic
  @Column(name = "document_type_value")
  private String documentTypeValue;

  @Basic
  @Column(name = "identifier_code")
  private String identifierCode;

  @Basic
  @Column(name = "issued_at")
  private LocalDate issuedAt;

  @Basic
  @Column(name = "issued_by")
  private String issuedBy;

  @Basic
  @Column(name = "issued_by_value")
  private String issuedByValue;

  @Basic
  @Column(name = "issued_place")
  private String issuedPlace;

  @Basic
  @Column(name = "issued_place_value")
  private String issuedPlaceValue;

  @Basic
  @Column(name = "priority")
  private boolean priority;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}
