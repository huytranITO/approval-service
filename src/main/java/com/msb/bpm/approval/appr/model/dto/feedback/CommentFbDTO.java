package com.msb.bpm.approval.appr.model.dto.feedback;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentFbDTO {

  private CustomerCommentFbDTO customer;
  private CreditCommentFbDTO applicationCredits;
  private IncomeCommentFbDTO applicationIncome;
  private AssetInfoCommentFbDTO assetInfo;
  private DocumentCommentFbDTO documents;
}
