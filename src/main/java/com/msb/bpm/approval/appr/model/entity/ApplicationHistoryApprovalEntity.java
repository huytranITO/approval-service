package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "application_history_approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationHistoryApprovalEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "executed_at")
  private LocalDateTime executedAt;

  @Basic
  @Column(name = "full_name")
  private String fullName;

  @Basic
  @Column(name = "username")
  private String username;

  @Basic
  @Column(name = "from_user_role")
  @Enumerated(EnumType.STRING)
  private ProcessingRole fromUserRole;

  @Basic
  @Column(name = "user_role")
  @Enumerated(EnumType.STRING)
  private ProcessingRole userRole;

  @Basic
  @Column(name = "reason")
  private String reason;

  @Basic
  @Column(name = "step_code")
  private String stepCode;

  @Basic
  @Column(name = "step_description")
  private String stepDescription;

  @Basic
  @Column(name = "proposal_approval_reception")
  private String proposalApprovalReception;

  @Basic
  @Column(name = "proposal_approval_reception_title")
  private String proposalApprovalReceptionTitle;

  @Basic
  @Column(name = "proposal_approval_user")
  private String proposalApprovalUser;

  @Basic
  @Column(name = "proposal_approval_full_name")
  private String proposalApprovalFullName;

  private String note;

  private String status;
}
