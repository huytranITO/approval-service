package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetScoreRBResponseData {
  private String typeOfModel;
  private Integer scoringId;
  private String identityNo;
  private List<String> statusRM;
  private List<String> statusApproval;
  private List<String> resultRM;
  private List<String> approvalResult;
  private List<String> timeRM;
  private List<String> approvalTime;
  private List<String> officerRM;
  private List<String> approvalOffice;
  private List<String> roleRM;
  private List<String> approvalRole;
  private List<String> scoreRM;
  private List<String> approvalScore;
  private List<String> rankRM;
  private List<String> approvalRank;
}
