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
@Table(name = "application_appraisal_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationAppraisalContentEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "content_type")
  private String contentType;

  @Basic
  @Column(name = "criteria_group")
  private String criteriaGroup;

  @Basic
  @Column(name = "criteria_group_value")
  private String criteriaGroupValue;

  @Basic
  @Column(name = "detail")
  private String detail;

  @Basic
  @Column(name = "detail_value")
  private String detailValue;

  @Basic
  @Column(name = "regulation")
  private String regulation;

  @Basic
  @Column(name = "management_measures")
  private String managementMeasures;

  @Basic
  @Column(name = "authorization")
  private String authorization;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}
