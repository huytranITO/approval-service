package com.msb.bpm.approval.appr.model.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "application_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationChecklistEntity extends BaseEntity {

  private static final long serialVersionUID = 7547313823256731829L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "application_id")
  private Long applicationId;

  @Basic
  @Column(name = "phase")
  private String phase;

  @Basic
  @Column(name = "`checklist_mapping_id`")
  private String checklistMappingId;
}
