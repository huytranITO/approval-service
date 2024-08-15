package com.msb.bpm.approval.appr.model.dto.cic;

import com.msb.bpm.approval.appr.model.dto.statisticfile.UploadFileResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedPDFResult {

  private boolean isSuccess = false;

  private boolean isAttackChecklist = false;

  private String clientQuestionId;

  private String queryAt;

  private String cicCode;

  private UploadFileResult uploadFileResult;

}
