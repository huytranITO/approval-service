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
@Table(name = "application_contact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationContactEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private Long id;

  @Basic
  @Column(table = "application_contact")
  private String relationship;

  @Basic
  @Column(table = "application_contact")
  private String relationshipTxt;

  @Basic
  @Column(table = "application_contact")
  private String fullName;

  @Basic
  @Column(table = "application_contact")
  private String phoneNumber;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;
}
