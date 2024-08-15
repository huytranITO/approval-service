package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.dto.css.ScoreRBCSS;

public interface CSSService {

  ScoreRBCSS getScoreRB(String profileId, String legalDocNo);

}
