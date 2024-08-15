package com.msb.bpm.approval.appr.model.dto.legacy;

import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBV2Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditRatingCssResponse {

  private GetScoreRBV2Response scoreRBV2Response;

  private String identityCard;

  private String profileId;

  private String customerType;
}
