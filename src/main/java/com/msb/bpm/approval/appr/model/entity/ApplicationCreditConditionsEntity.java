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
@Table(name = "application_credit_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditConditionsEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "segment")
  private String segment;

  @Basic
  @Column(name = "`group`")
  private String group;

  @Basic
  @Column(name = "group_value")
  private String groupValue;

  @Basic
  @Column(name = "code")
  private String code;

  @Basic
  @Column(name = "code_value")
  private String codeValue;

  @Basic
  @Column(name = "detail")
  private String detail;

  @Basic
  @Column(name = "time_of_control")
  private String timeOfControl;

  @Basic
  @Column(name = "time_of_control_value")
  private String timeOfControlValue;

  @Basic
  @Column(name = "applicable_subject")
  private String applicableSubject;

  @Basic
  @Column(name = "applicable_subject_value")
  private String applicableSubjectValue;

  @Basic
  @Column(name = "control_unit")
  private String controlUnit;

  @Basic
  @Column(name = "control_unit_value")
  private String controlUnitValue;

  @Basic
  @Column(name = "time_control_disburse")
  private String timeControlDisburse;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "credit_condition_id")
  private Long creditConditionId;
}
