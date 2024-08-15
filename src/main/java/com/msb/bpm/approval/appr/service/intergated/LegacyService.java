package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBV2Response;

public interface LegacyService {
  GetScoreRBV2Response getScoringData (String profileId, String legalDocNo);

}
