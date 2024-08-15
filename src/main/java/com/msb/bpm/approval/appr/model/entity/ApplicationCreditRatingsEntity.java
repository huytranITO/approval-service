package com.msb.bpm.approval.appr.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application_credit_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditRatingsEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "rating_system")
  private String ratingSystem;

  @Basic
  @Column(name = "rating_id")
  private String ratingId;

  @Basic
  @Column(name = "rating_result")
  private String ratingResult;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "request_id")
  private String requestId;

  @Basic
  @Column(name = "approval_comment")
  private String approvalComment;

  @Basic
  @Column(name = "recommendation")
  private String recommendation;

  @OneToMany(mappedBy = "applicationCreditRating", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE}
  , orphanRemoval = true)
  private Set<ApplicationCreditRatingsDtlEntity> creditRatingsDtls = new HashSet<>();
}
