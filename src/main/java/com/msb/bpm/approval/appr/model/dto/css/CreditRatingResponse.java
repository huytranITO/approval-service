package com.msb.bpm.approval.appr.model.dto.css;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditRatingResponse {

    private ScoreRBCSS scoreRbCss;

    private String identityCard;

    private String profileId;

    private String customerType;

}
