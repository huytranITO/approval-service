package com.msb.bpm.approval.appr.model.dto.css;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ScoreRBCSS {

  private List<BigDecimal> scoresRM;

  private List<BigDecimal> approvalScores;

  private List<String> rankRM;

  private List<String> approvalRank;
}
