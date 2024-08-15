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
@Table(name = "application_phone_expertise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationPhoneExpertiseEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "person_answer")
  private String personAnswer;

  @Basic
  @Column(name = "person_answer_value")
  private String personAnswerValue;

  @Basic
  @Column(name = "phone_number")
  private String phoneNumber;

  @Basic
  @Column(name = "called_at")
  private String calledAt;

  @Basic
  @Column(name = "note")
  private String note;

  @Basic
  @Column(name = "ext")
  private String ext;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}