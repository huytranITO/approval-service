package com.msb.bpm.approval.appr.model.entity;

import lombok.*;

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
@Table(name = "application_credit_ratings_dtl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class ApplicationCreditRatingsDtlEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_credit_ratings_id", referencedColumnName = "id")
  private ApplicationCreditRatingsEntity applicationCreditRating;

  @Basic
  @Column(name = "identity_card")
  private String identityCard;

  @Basic
  @Column(name = "score")
  private Double score;

  @Basic
  @Column(name = "`rank`")
  private String rank;

  @Basic
  @Column(name = "executor")
  private String executor;

  @Basic
  @Column(name = "`role`")
  private String role;

  @Basic
  @Column(name = "status")
  private String status;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "scoring_id")
  private Integer scoringId;

  @Basic
  @Column(name = "identity_no")
  private String identityNo;

  @Basic
  @Column(name = "type_of_model")
  private String typeOfModel;

  @Basic
  @Column(name = "scoring_source")
  private String scoringSource;

  @Basic
  @Column(name = "recommendation")
  private String recommendation;

  @Basic
  @Column(name = "scoring_time")
  private String scoringTime;

  @Basic
  @Column(name = "status_description")
  private String statusDescription;

  @Basic
  @Column(name = "approval_comment")
  private String approvalComment;
}
