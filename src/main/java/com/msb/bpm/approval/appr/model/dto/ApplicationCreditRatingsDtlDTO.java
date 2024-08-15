package com.msb.bpm.approval.appr.model.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCreditRatingsDtlDTO {

  private Long id;

  private String identityCard;

  private Double score;

  private String rank;

  private String executor;

  private String role;

  private String status;

  private LocalDateTime createdAt;

  private Integer orderDisplay;

  private Integer scoringId;

  private String identityNo;

  private String typeOfModel;

  private String scoringSource;

  private String recommendation;

  private String scoringTime;

  private String approvalComment;

  private String statusDescription;

  private LocalDateTime scoringDateTime;
}
