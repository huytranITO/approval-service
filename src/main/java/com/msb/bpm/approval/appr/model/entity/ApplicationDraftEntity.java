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
import javax.persistence.Table;

@Entity
@Table(name = "application_draft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationDraftEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Basic
  @Column(name = "bpm_id")
  private String bpmId;

  @Basic
  @Column(name = "tab_code")
  private String tabCode;

  @Basic
  @Column(name = "data")
  private byte[] data;

  @Basic
  @Column(name = "processing_role")
  private String processingRole;

  @Basic
  @Column(name = "status")
  private Integer status;
}
