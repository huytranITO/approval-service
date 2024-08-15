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
import java.time.LocalDateTime;

@Entity
@Table(name = "application_history_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationHistoryFeedbackEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "user_role")
  private String userRole;

  @Basic
  @Column(name = "username")
  private String username;

  @Basic
  @Column(name = "feedback_at")
  private LocalDateTime feedbackAt;

  @Basic
  @Column(name = "feedback_content")
  private String feedbackContent;

  @Basic
  @Column(name = "created_phone_number")
  private String createdPhoneNumber;

  @Basic
  @Column(name = "comment")
  private byte[] comment;
}
